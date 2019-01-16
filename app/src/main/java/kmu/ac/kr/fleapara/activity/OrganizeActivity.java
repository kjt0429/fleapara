package kmu.ac.kr.fleapara.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import kmu.ac.kr.fleapara.R;
import kmu.ac.kr.fleapara.RecyclerItemClickListener;
import kmu.ac.kr.fleapara.model.MarketModel;
import kmu.ac.kr.fleapara.model.UserModel;

public class OrganizeActivity extends AppCompatActivity {

    MarketModel marketModel;

    ArrayList<UserModel> userModels = new ArrayList<>();

    UserRecyclerViewAdapter adapter;

    TextView attedTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organize);

        //  툴바 생성
        Toolbar mToolbar = (Toolbar) findViewById(R.id.ToolBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //  툴바에 홈 버튼 활성화
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //  intent를 통해 이전화면에서 넘겨준 marketModel을 받음.
        Intent intent = getIntent();
        marketModel = (MarketModel) intent.getSerializableExtra("market");

        TextView titleTextView = (TextView) findViewById(R.id.titleTextView);
        titleTextView.setText(marketModel.name);

        attedTextView = (TextView)findViewById(R.id.attedNumTextView);
        attedTextView.setText(String.valueOf(marketModel.users.size())+" 명");

        //  excel 내보내기 Btn
        Button excelBtn = (Button)findViewById(R.id.excelBtn);
        excelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveExcel();
            }
        });

        //  push Message
        Button pushBtn = (Button)findViewById(R.id.pushBtn);
        pushBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OrganizeActivity.this,PushActivity.class));
            }
        });


        //  createRecyclerView
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        adapter = new UserRecyclerViewAdapter(userModels);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onLongItemClick(View view, final int position) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(OrganizeActivity.this);

                //  유저상태가 대기 일때
                if(marketModel.users.get(userModels.get(position).uid) == false) {
                    dialog.setMessage(
                            userModels.get(position).name +
                                    " 님을 승인 하시겠습니까?");

                    dialog.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            marketModel.users.put(userModels.get(position).uid, true);
                            FirebaseDatabase.getInstance().getReference("market").child(marketModel.uid).setValue(marketModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    });

                    dialog.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                }

                //  유저상태가 승인 일때
                else if(marketModel.users.get(userModels.get(position).uid) == true) {
                    dialog.setMessage(
                            userModels.get(position).name +
                                    " 님을 취소 하시겠습니까?");

                    dialog.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            marketModel.users.put(userModels.get(position).uid, false);
                            FirebaseDatabase.getInstance().getReference("market").child(marketModel.uid).setValue(marketModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    });

                    dialog.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                }


                dialog.show();
            }
        }));
    }

    private void saveExcel(){
        Workbook workbook = new HSSFWorkbook();

        Sheet sheet = workbook.createSheet(); // 새로운 시트 생성
        Row row = sheet.createRow(0); // 새로운 행 생성
        Cell cell;

        cell = row.createCell(0); // 1번 셀 생성
        cell.setCellValue("이름"); // 1번 셀 값 입력

        cell = row.createCell(1); // 2번 셀 생성
        cell.setCellValue("email"); // 2번 셀 값 입

        cell = row.createCell(2); // 2번 셀 생성
        cell.setCellValue("tel"); // 2번 셀 값 입

        cell = row.createCell(3); // 2번 셀 생성
        cell.setCellValue("상태"); // 2번 셀 값 입

        for(int i = 0; i < userModels.size() ; i++){ // 데이터 엑셀에 입력
            row = sheet.createRow(i+1);
            cell = row.createCell(0);
            cell.setCellValue(userModels.get(i).name);
            cell = row.createCell(1);
            cell.setCellValue(userModels.get(i).email);
            cell = row.createCell(2);
            cell.setCellValue(userModels.get(i).tel);
            cell = row.createCell(3);
            if(marketModel.users.get(userModels.get(i).uid) == true){
                cell.setCellValue("승인");
            }else if(marketModel.users.get(userModels.get(i).uid) == false){
                cell.setCellValue("대기");
            }
        }

        File xlsFile = new File(getExternalFilesDir(null),"flealist.xls");
        try{
            FileOutputStream os = new FileOutputStream(xlsFile);
            workbook.write(os); // 외부 저장소에 엑셀 파일 생성
        }catch (IOException e){
            e.printStackTrace();
        }
        Toast.makeText(getApplicationContext(),xlsFile.getAbsolutePath()+"에 저장되었습니다",Toast.LENGTH_SHORT).show();

        Uri path = Uri.fromFile(xlsFile);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("application/excel");
        shareIntent.putExtra(Intent.EXTRA_STREAM,path);
        startActivity(Intent.createChooser(shareIntent,"엑셀 내보내기"));
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

    @Override
    protected void onResume() {
        super.onResume();
        getUserData();
    }

    //  행사에 참가한 유저 정보 가져오기.
    public void getUserData() {
        userModels.clear();

        ArrayList<String> key = new ArrayList<>();
        key.clear();

        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("users");

        //  HashMap에서 Key Value 가져오기.
        for (Map.Entry<String, Boolean> entry : marketModel.users.entrySet()) {
            Log.d("getHashKey", entry.getKey());
            key.add(entry.getKey());
        }

        //  가져온 Key(참가신청인원) 수 만큼 반복 돌려 UserData 가져오기.
        for (int i = 0; i < key.size(); i++) {
            mRef.child(key.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    UserModel userModel = dataSnapshot.getValue(UserModel.class);
                    userModels.add(userModel);

                    adapter.notifyDataSetChanged();
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


    }

    class UserRecyclerViewAdapter extends RecyclerView.Adapter<UserRecyclerViewAdapter.ItemViewHolder> {

        ArrayList<UserModel> mItem;

        public UserRecyclerViewAdapter(ArrayList<UserModel> userModels) {
            mItem = userModels;

            Log.d("get", String.valueOf(mItem.size()));
        }


        @NonNull
        @Override
        public UserRecyclerViewAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_userlist_recyclerview, parent, false);

            return new UserRecyclerViewAdapter.ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull UserRecyclerViewAdapter.ItemViewHolder holder, int position) {

            if (mItem.get(position).profileImageUri != null) {
                Glide.with(holder.itemView.getContext())
                        .load(mItem.get(position).profileImageUri)
                        .apply(new RequestOptions().circleCrop())
                        .into(holder.profilImageView);
            } else {
                Glide.with(holder.itemView.getContext())
                        .load(R.drawable.img_profile_tmp)
                        .apply(new RequestOptions().circleCrop())
                        .into(holder.profilImageView);
            }


            holder.nameTextView.setText(
                    mItem.get(position).name +
                            " [" +
                            mItem.get(position).nickname +
                            "]"
            );
            holder.emailTextView.setText(mItem.get(position).email);
            holder.telTextView.setText(mItem.get(position).tel);

            //  승인된 유저
            if (marketModel.users.get(mItem.get(position).uid) == true) {
                holder.stateTextView.setBackgroundResource(R.color.colorBlue);
                holder.stateTextView.setText("승  인");
            } else if (marketModel.users.get(mItem.get(position).uid) == false) {
                holder.stateTextView.setBackgroundResource(R.color.colorMiddleGray);
                holder.stateTextView.setText("대기중");
            }

        }

        @Override
        public int getItemCount() {
            return mItem.size();
        }

        class ItemViewHolder extends RecyclerView.ViewHolder {

            private ImageView profilImageView;
            private TextView nameTextView;
            private TextView emailTextView;
            private TextView telTextView;
            private TextView stateTextView;

            public ItemViewHolder(View itemView) {
                super(itemView);
                profilImageView = (ImageView) itemView.findViewById(R.id.profileImageView);
                nameTextView = (TextView) itemView.findViewById(R.id.nameTextView);
                emailTextView = (TextView) itemView.findViewById(R.id.emailTextView);
                telTextView = (TextView) itemView.findViewById(R.id.telTextView);
                stateTextView = (TextView) itemView.findViewById(R.id.stateTextView);

            }

        }
    }

}
