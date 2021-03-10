package com.vilaca.niverremember.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.vilaca.niverremember.R;
import com.vilaca.niverremember.config.ConfiguracaoFirebase;
import com.vilaca.niverremember.helper.Permissao;
import com.vilaca.niverremember.helper.UsuarioFirebase;
import com.vilaca.niverremember.model.Aniversario;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.Random;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddAniversarioActivity extends AppCompatActivity {

    private static final int SELECAO_CAMERA = 100;
    private static final int SELECAO_GALERIA = 200;

    private TextInputEditText campoData, campoNome, campoIdade;
    private CircleImageView circleImageViewPerfil;
    private ImageButton imageButtonCamera;
    private ImageButton imageButtonGaleria;

    private Aniversario aniversario;
    private String identificadorUsuario;
    private String urlFoto;
    private Bitmap imagemAniversario;
    private String chave;
    private Boolean apagarImagem = true;

    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_aniversario);

        identificadorUsuario = UsuarioFirebase.getIdentifacadorUsuario();
        chave = geradorChaves();

        Permissao.validarPermissoes(permissoesNecessarias, this, 1);

        campoNome = findViewById(R.id.editNomeAddNiver);
        campoData = findViewById(R.id.editDataAddNiver);
        campoIdade = findViewById(R.id.editIdadeAddNiver);
        circleImageViewPerfil = findViewById(R.id.circleImageViewFotoPerfil);
        imageButtonCamera = findViewById(R.id.imageButtonCamera);
        imageButtonGaleria = findViewById(R.id.imageButtonGaleria);

        campoData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selecionarAniversario();
            }
        });

        imageButtonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(intent.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(intent, SELECAO_CAMERA);
                }
            }
        });

        imageButtonGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if(intent.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(intent, SELECAO_GALERIA);
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            Bitmap imagem = null;

            try {

                switch (requestCode){
                    case SELECAO_CAMERA:
                        imagem = (Bitmap) data.getExtras().get("data");
                        break;
                    case SELECAO_GALERIA:
                        Uri localImagemSelecionda = data.getData();
                        if (android.os.Build.VERSION.SDK_INT >= 29){
                            imagem = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), localImagemSelecionda));
                        }else imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionda);
                        break;
                }

                if(imagem != null){
                    circleImageViewPerfil.setImageBitmap(imagem);
                    imagemAniversario = imagem;
                    salvarImagemNuvem(imagemAniversario);
                }

            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for(int permissaoResultado : grantResults){
            if(permissaoResultado == PackageManager.PERMISSION_DENIED){
                alertaValidacaoPermissao();
            }
        }

    }

    private void alertaValidacaoPermissao(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para utilizar o app é necessário aceitar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void selecionarAniversario(){

        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, android.app.AlertDialog.THEME_HOLO_LIGHT,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        campoData.setText(dayOfMonth + "/" + (monthOfYear + 1));

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().findViewById(Resources.getSystem().getIdentifier("year", "id", "android")).setVisibility(View.GONE);
        datePickerDialog.show();

    }

    private void salvarImagemNuvem(Bitmap imagem){

        StorageReference storageReference = ConfiguracaoFirebase.getfirebaseStorage();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] dadosImagem = baos.toByteArray();

        final StorageReference imagemRef = storageReference
                .child("imagens")
                .child(identificadorUsuario)
                .child(chave + ".jpeg");

        UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddAniversarioActivity.this,"Erro ao fazer upload da imagem",Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AddAniversarioActivity.this,"Sucesso ao fazer upload da imagem",Toast.LENGTH_SHORT).show();
                imagemRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        urlFoto = uri.toString();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddAniversarioActivity.this,"Erro ao receber URL da imagem",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void fechar(View view){
        finish();
    }

    public void salvarAniversario(View view){
        if(validarCampos()){
            aniversario = new Aniversario();
            String textoIdade = campoIdade.getText().toString();

            aniversario.setNome(campoNome.getText().toString());
            aniversario.setData(campoData.getText().toString());
            if(!textoIdade.isEmpty()){
                aniversario.setIdade(Integer.parseInt(textoIdade));
            }else aniversario.setIdade(Aniversario.IDADE_NULA);

            if(urlFoto != null){
               aniversario.setFoto(urlFoto);
            }

            aniversario.salvar();
            apagarImagem = false;
            finish();
        }

    }

    public Boolean validarCampos(){

        String textoNome= campoNome.getText().toString();
        String textoData = campoData.getText().toString();

        if(!textoNome.isEmpty()){

        }else {
            Toast.makeText(AddAniversarioActivity.this, "Nome não foi preenchido", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!textoData.isEmpty()){

        }else {
            Toast.makeText(AddAniversarioActivity.this, "Data não foi preenchida", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public String geradorChaves(){
        return UUID.randomUUID().toString();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(apagarImagem){
            StorageReference storageReference = ConfiguracaoFirebase.getfirebaseStorage();
            storageReference.child("imagens")
                            .child(identificadorUsuario)
                            .child(chave + ".jpeg")
                            .delete();
        }
    }
}
