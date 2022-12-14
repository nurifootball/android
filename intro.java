package nrfb.nrfb__.nrfb;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;

import nrfb.nrfb.R;

public class intro extends FragmentActivity {

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        mContext = this;
        ImageView iv = findViewById(R.id.ex1);
        Glide.with(this)
                .load(R.drawable.gif)

                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(iv);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationChannel check_default = notificationManager.getNotificationChannel("default");

            if (check_default == null) {
                NotificationChannel channel = new NotificationChannel("default", "기본알림", NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription("기본 알림채널입니다.\n불필요한 알림은 보내지않으니 항상 ON 해주세요.");
                channel.enableLights(true);
                channel.setLightColor(Color.BLUE);
                channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                notificationManager.createNotificationChannel(channel);
            }
        }


        FirebaseApp.initializeApp(this);
        FirebaseInstanceId.getInstance().getInstanceId();

        checkPermission();
    }


    private void checkPermission(){
        int camera = ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA);
        int storage = ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if(
                camera == PackageManager.PERMISSION_DENIED ||
                storage == PackageManager.PERMISSION_DENIED
        ){

            LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            View parent = inflater.inflate(R.layout.dialog_permission, null);
            LinearLayout ll_List1 = ((LinearLayout)parent.findViewById(R.id.ll_List1));

            ll_List1.addView(getPermissionItem(inflater,"카메라", "글 작성시 사진 업로드를 위해 사용됩니다.", R.drawable.icon_permision_camera));
            ll_List1.addView(getPermissionItem(inflater,"저장공간", "앨범사진을 업로드하거나, 앱 이용 데이터를 저장하는데 이용됩니다.", R.drawable.icon_permision_storage));

            AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
            alert.setView(parent);
            alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();     //닫기
                }
            });
            alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    ActivityCompat.requestPermissions(intro.this, new String[]{
//                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                            Manifest.permission.ACCESS_FINE_LOCATION
                    }, 0);
                }
            });
            alert.show();

        }
        else{

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(intro.this, MainActivity.class);
                    if(getIntent().hasExtra("link")){
                        intent.putExtra("link", getIntent().getStringExtra("link"));
                    }
                    startActivity(intent);
                    overridePendingTransition(0,0);
                    finish();
                }
            }, 1000);

        }

    }



    private View getPermissionItem(LayoutInflater inflater, String title, String text, int icon ){
        View item = inflater.inflate(R.layout.item_permission, null);
        ((TextView)item.findViewById(R.id.tv_Title)).setText(title);
        ((TextView)item.findViewById(R.id.tv_Text)).setText(text);
        ((ImageView)item.findViewById(R.id.iv_Icon)).setImageResource(icon);
        return item;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == 0){
            boolean granted = true;
            for(int i = 0; i < grantResults.length; i++){
                if(grantResults[i] == 0){
                    Log.e("권한"+i, "승인");
                }
                else{
                    //권한 거절
                    Log.e("권한"+i, "거절");
                    granted = false;
                }
            }
            if(!granted){
                Toast.makeText(mContext, "앱 필수 권한이 거부되었습니다.\n앱을 이용하시려면 권한을 허용해야합니다.", Toast.LENGTH_LONG).show();
                finish();
            }
            else{
                checkPermission();
            }

        }
    }
}