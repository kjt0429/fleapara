package kmu.ac.kr.fleapara.activity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import kmu.ac.kr.fleapara.R;
import kmu.ac.kr.fleapara.model.MarketModel;

/*
 * 갤러리에서 이미지를 가져와서 위험권한 확인후,
 * uri를 FileProvider.getUriForFile()로 가져옴
 * 몇 확률로 이미지가 돌아가는데 이미지를 원 상태로 돌리는 코드 포함
 * */
public class AddMarketActivity extends AppCompatActivity {

    EditText nameEditText;
    EditText costEditText;
    EditText lineinfoEditText;
    EditText infoEditText;

    TextView deadlineTextView;
    ImageView profileImageView;

    private final int CAMERA_CODE = 1111;
    private final int GALLERY_CODE = 1112;
    private final int REQUEST_PERMISSION_CODE = 2222;

    private Uri photoUri;
    public Uri firebaseUri;

    private String currentPhotoPath;//실제 사진 파일 경로
    private String mImageCaptureName;//이미지 이름

    public MarketModel marketModel = new MarketModel();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_market);

        nameEditText = (EditText) findViewById(R.id.nameEditText);
        costEditText = (EditText) findViewById(R.id.costEditText);
        lineinfoEditText = (EditText) findViewById(R.id.lineinfoEditText);
        infoEditText = (EditText) findViewById(R.id.infoEditText);


        createToolbar();            //  툴바 생성
        createAreaSpinner();        //  지역 스피너 생성
        createCategorySpinner();    //  카테고리 스피너 생성

        deadlineTextView = (TextView) findViewById(R.id.deadlineTextView);
        deadlineTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPickerDate();
            }
        });

        profileImageView = (ImageView) findViewById(R.id.profile_img);


        //  갤러리에서 사진을 가져올때, 권한체크하고 URI를 따로 싸서 가져옴
        TextView galleryBtn = (TextView) findViewById(R.id.galleryBtn);
        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permissionCheck = ContextCompat.checkSelfPermission(AddMarketActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE);

                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    selectGallery();


                } else {
                    requestPermission();
                    if (ActivityCompat.shouldShowRequestPermissionRationale(AddMarketActivity.this
                            , android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
//거부했을 경우
                        Toast toast = Toast.makeText(AddMarketActivity.this,
                                "기능 사용을 위한 권한 동의가 필요합니다.", Toast.LENGTH_SHORT);
                        toast.show();
                    } else {

                        Toast toast = Toast.makeText(AddMarketActivity.this,
                                "기능 사용을 위한 권한 동의가 필요합니다.", Toast.LENGTH_SHORT);
                        toast.show();
                    }

                }
            }

        });


        Button addButton = (Button) findViewById(R.id.addBtn);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //  공백 체크
                if (nameEditText.getText().toString().equals("") ||
                        deadlineTextView.getText().toString().equals("") ||
                        costEditText.getText().toString().equals("") ||
                        lineinfoEditText.getText().toString().equals("") ||
                        infoEditText.getText().toString().equals("")) {
                    Toast.makeText(AddMarketActivity.this, "공백을 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(getApplicationContext(), "등록중 입니다.", Toast.LENGTH_LONG).show();

                marketModel.name = nameEditText.getText().toString();
                marketModel.deadlineTime = deadlineTextView.getText().toString();

                double cost = Double.parseDouble(costEditText.getText().toString());
                DecimalFormat df = new DecimalFormat("#,##0");
                marketModel.cost = df.format(cost);

                marketModel.lineinfo = lineinfoEditText.getText().toString();
                marketModel.info = infoEditText.getText().toString();

                //  개시 시간
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String getTime = sdf.format(date);
                marketModel.startTime = getTime;


                marketModel.organizerUid = FirebaseAuth.getInstance().getCurrentUser().getUid();  // 주최자


                marketModel.uid = FirebaseDatabase.getInstance().getReference().child("market").child(marketModel.category)
                        .push().getKey();


                //  uri를 firebase Storage에 저장
                FirebaseStorage.getInstance().getReference().child("marketImages").child(marketModel.uid).putFile(firebaseUri)
                        .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                marketModel.profileImageUri = task.getResult().getDownloadUrl().toString();



                                FirebaseDatabase.getInstance().getReference().child("market").child(marketModel.uid)
                                        .setValue(marketModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        finish();

                                    }
                                });




                            }
                        });




            }
        });

    }


    private void createToolbar() {
        Toolbar mToolbar;
        mToolbar = (Toolbar) findViewById(R.id.ToolBar);
        setSupportActionBar(mToolbar);                              //  Toolbar를 Actionbar로 설정
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);      //  HomeBtn 활성화
        getSupportActionBar().setDisplayShowTitleEnabled(false);    //  Title 숨기기
    }

    private void createAreaSpinner() {
        final String[] area = getResources().getStringArray(R.array.area);

        Spinner areaSpinner = (Spinner) findViewById(R.id.areaSpinner);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.area, R.layout.custom_spinner_item);
        areaSpinner.setAdapter(adapter);

        //스피너 이벤트 발생
        areaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                marketModel.area = area[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void createCategorySpinner() {
        final String[] category = getResources().getStringArray(R.array.category);

        Spinner categorySpinner = (Spinner) findViewById(R.id.categorySpinner);
        ArrayAdapter categoryAdapter = ArrayAdapter.createFromResource(this, R.array.category, R.layout.custom_spinner_item);
        categorySpinner.setAdapter(categoryAdapter);

        //스피너 이벤트 발생
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                marketModel.category = category[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void createPickerDate() {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(AddMarketActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int date) {

                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DATE, date);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String strDate = sdf.format(cal.getTime());

                deadlineTextView.setText(strDate);
            }
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
        dialog.getDatePicker().setMinDate(new Date().getTime());
        dialog.show();
    }


    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_PERMISSION_CODE);

    }


    private void selectPhoto() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {

                }
                if (photoFile != null) {
                    photoUri = FileProvider.getUriForFile(this, getPackageName(), photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(intent, CAMERA_CODE);
                }
            }

        }
    }

    private File createImageFile() throws IOException {
        File dir = new File(Environment.getExternalStorageDirectory() + "/path/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        mImageCaptureName = timeStamp + ".png";

        File storageDir = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/path/"

                + mImageCaptureName);
        currentPhotoPath = storageDir.getAbsolutePath();

        return storageDir;
    }

    private void selectGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {

            case REQUEST_PERMISSION_CODE:

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //동의 했을 경우
                    selectGallery();
                } else {
                    //거부했을 경우
                    Toast toast = Toast.makeText(this,
                            "기능 사용을 위한 권한 동의가 필요합니다.", Toast.LENGTH_SHORT);
                    toast.show();
                }

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            sendPicture(data.getData());
            firebaseUri = data.getData();
        }
    }

    private void sendPicture(Uri imgUri) {

        String imagePath = getRealPathFromURI(imgUri); // path 경로
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        int exifDegree = exifOrientationToDegrees(exifOrientation);

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);//경로를 통해 비트맵으로 전환
        profileImageView.setImageBitmap(rotate(bitmap, exifDegree));//이미지 뷰에 비트맵 넣기

    }


    private int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private Bitmap rotate(Bitmap src, float degree) {
        // Matrix 객체 생성
        Matrix matrix = new Matrix();
        // 회전 각도 셋팅
        matrix.postRotate(degree);
        // 이미지와 Matrix 를 셋팅해서 Bitmap 객체 생성
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(),
                src.getHeight(), matrix, true);
    }

    private String getRealPathFromURI(Uri contentUri) {
        int column_index = 0;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }

        return cursor.getString(column_index);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //toolbar의 back키 눌렀을 때 동작
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }


}