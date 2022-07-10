package com.lux.ex088firebasestorage;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lux.ex088firebasestorage.databinding.ActivityMainBinding;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    //Firebase : 서버쪽 작업을 대신해주는 Google 서비스

    //처음 시작은 Firebase console 에서 [프로젝트 만들기]를 통해 작업순서대로
    //이 프로젝트와 firebase 서비스를 연동하기 즉, 라이브러리 추가

    ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnLoad.setOnClickListener(view -> clickLoad());
        binding.btnSelect.setOnClickListener(view -> clickSelect());
        binding.btnUpload.setOnClickListener(view -> clickUpload());

    }

    void clickLoad(){
        //Firebase Storage 에 저장되어 있는 이미지 파일을 읽어오기

        //Firebase Storage 관리 객체 소환
        FirebaseStorage firebaseStorage=FirebaseStorage.getInstance();

        //root(최상위) 참조 객체 얻어오기
        StorageReference rootRef=firebaseStorage.getReference();

        //읽어오길 원하는 파일의 참조객체 얻어오기
        StorageReference imgRef=rootRef.child("IMG_8059.JPG");
        imgRef=rootRef.child("photos/sydney.jpg");  //하위폴더

        //파일 참조객체로부터 이미지의 URL 얻어오기
        //[firebase console 에서 해당 파일을 선택하면 오른쪽에 파일 위치 섹션의 '액세스 토큰과 함꼐 다운로드 url'이 이미지의 url]
        if (imgRef!=null){
            //참조객체로부터 다운로드 url을 얻어오는 작업을 수행하고 성공되었다면 콜백 리스너 등록
            imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {    //다운로드 url이 파라미터로 전달됨
                    Glide.with(MainActivity.this).load(uri).into(binding.iv);
                }
            });
        }
    }

    void clickSelect(){
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        resultLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> resultLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode()!=RESULT_OK) return;

            //결과를 가져온 Intent에게 객체를 얻어오고 곧바로 uri 데이터까지 얻어오기
            imgUri=result.getData().getData();
            Glide.with(MainActivity.this).load(imgUri).into(binding.iv);
        }
    });

    //사진 앱을 통해서 선택된 이미지의 content 경로 uri 의 멤버변수
    Uri imgUri=null;

    void clickUpload(){
        //firebase storage 에 파일 업로드
        //storage 는 파일의 절대경로[실제 경로] 없이 Uri(Content 경로)로 업로드 가능함.

        //Firebase Storage에 대한 관리 객체 소환
        FirebaseStorage firebaseStorage=FirebaseStorage.getInstance();

        //파일명이 중복되면 덮어쓰기가 되므로 중복되지 않는 이름을 선호함.
        //보통은 날짜를 이용함.
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddhhmmss");
        String fileName="IMG_"+sdf.format(new Date())+".png";   //원래는 원본 파일명에서 확장자를 얻어와야 하지만 절대경로 코드가
        //번거로워서 그냥 png로 정함.


        //저장한 파일의 위치에 대한 참조객체 얻어오기
        StorageReference imgRef=firebaseStorage.getReference("uploads/"+fileName);  //"uploads"라는 폴더가 없으면 만들어 줌.

        //선택한 이미지(imgUri)를 imgRef 참조객체가 참조하는 파일위치에 저장(upload)
        //imgRef.putFile(imgUri);

        //업로드 성공 여부를 알고싶다면
        UploadTask uploadTask =imgRef.putFile(imgUri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(MainActivity.this, "upload success", Toast.LENGTH_SHORT).show();
            }
        });
    }




}