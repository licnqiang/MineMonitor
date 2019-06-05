package cn.piesat.minemonitor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.piesat.minemonitor.db.CustomSQLTools;
import cn.piesat.minemonitor.home.Constant;
import cn.piesat.minemonitor.mapdata.utils.Config;
import cn.piesat.minemonitor.mapdata.utils.FieldListDialogUtils;
import cn.piesat.minemonitor.mapdata.utils.SpHelper;
import cn.piesat.minemonitor.util.CompressOperate_zip4j;
import cn.piesat.minemonitor.util.DrawableSwitch;
import cn.piesat.minemonitor.util.FileUtil;
import cn.piesat.minemonitor.util.ToastUtil;

/**
 * 登录页面
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {
    private Button login;
    private Intent intent;
    @BindView(R.id.tv_registered)
    TextView tv_registered;
    @BindView(R.id.et_user_name)
    EditText user_name;
    @BindView(R.id.et_password)
    EditText password;
    private DrawableSwitch ds_all_clean;
    private SharedPreferences sp;
    private CustomSQLTools s;
    private SharedPreferences.Editor editor;
    private CompressOperate_zip4j zip4j;
    private List<File> list;
    public static String encryptDir = "VerifyData";
    public static String encrypt2 = encryptDir;
    private int tag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SpHelper.setStringValue("FIELD", "VerifyData");
        s = new CustomSQLTools();
        sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        if (sp.getBoolean("LOGINTAG", true)) {
            editor = sp.edit();
            editor.putBoolean("LOGINTAG", false);
            editor.putString("FIELD1", "0");
            editor.commit();
        }
        ButterKnife.bind(this);
        initView();
        onClickListener();
    }

    private void onClickListener() {
        login.setOnClickListener(this);
    }

    private void initView() {
        user_name.setText("");
        password.setText("");
        login = findViewById(R.id.btn_login);
        ds_all_clean = findViewById(R.id.ds_all_clean);
        tv_registered.setOnClickListener(this);
        if (sp.getBoolean("isSwitchOn", false)) {
            user_name.setText(sp.getString("username", ""));
            password.setText(sp.getString("password", ""));
            ds_all_clean.setSwitchOn(true);
        }
       /* if(ds_all_clean.isSwitchOn()){

        }*/

        ds_all_clean.setListener(new DrawableSwitch.MySwitchStateChangeListener() {
            @Override
            public void mySwitchStateChanged(boolean isSwitchOn) {
                if (!isSwitchOn) {
//                    user_name.setText("");
                    password.setText("");
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_registered:
                List<String> setFileName = new ArrayList<>();
                List<String> setFileName1 = new ArrayList<>();
                setFileName.clear();
                setFileName1.clear();
                setFileName.addAll(s.getFileName());
                for (int i = 0; i < setFileName.size(); i++) {
                    if (setFileName.get(i).contains("/VerifyData")) {
                        setFileName1.add(setFileName.get(i).substring(setFileName.get(i).indexOf("/V") + 1));
                    }

                }
                final FieldListDialogUtils dialog = new FieldListDialogUtils(MainActivity.this);
                dialog.showDialog1(setFileName1, new FieldListDialogUtils.OnItemFieldListener() {
                    @Override
                    public void getFieldName(String field) {
                        String path = Config.getProjectPath() + "/default/config";
                        String sqlData = Config.getProjectPath() + "/default/data/" + field + "/nmmvsqlite.db";
                        String sqlData1 = Config.getProjectPath() + "/default/data/" + field + "/db.zip";
                        File file = new File(sqlData1);
                        if (!file.exists()) {
                            Toast.makeText(MainActivity.this, "数据未切换成功!", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            Toast.makeText(MainActivity.this, field + "数据切换成功!", Toast.LENGTH_SHORT).show();
                            deleteFile(new File(path));
                            SpHelper.setStringValue("FIELD", field);

                        }
                        //deleteAllFiles(new File(path));

                    }
                });
                break;
            case R.id.btn_login:
                SpHelper.setStringValue("RUNXY", "2");
                /**
                 * 压缩数据库文件
                 * 解压数据库文件
                 *
                 * */
                encryptDir = SpHelper.getStringValue("FIELD");
                if (encryptDir.equals("")) {
                    encryptDir = "VerifyData";
                }
                editor = sp.edit();
                editor.putString("FIELD1", encryptDir);
                editor.commit();
                zip4j = new CompressOperate_zip4j();
                list = new ArrayList<>();
//                list.add(0, new File(FileUtil.encrypt+encryptDir+FileUtil.encryptDB));
//                list.add(1, new File(FileUtil.encrypt+encryptDir+FileUtil.encryptDBJ));
//                list.add(2, new File(FileUtil.encrypt+encryptDir+FileUtil.encryptTEX));
//                zip4j.compressZip4j(list, FileUtil.encrypt+encryptDir, "123456");
//                FileUtil.deletefile(list);
                zip4j = new CompressOperate_zip4j();
                if (zip4j.uncompressZip4j(FileUtil.encrypt + encryptDir + FileUtil.encryptZIP, FileUtil.encrypt + encryptDir, "123456") == -1) {
                    ToastUtil.show(this, "解密失败,请切换数据后重试!");

                } else {
                    String name = user_name.getText().toString().trim();
                    String pwd = password.getText().toString().trim();
                    if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(pwd)) {
                        String employee_name = new CustomSQLTools().getPwdName(name, pwd, this);
                        if (!TextUtils.isEmpty(employee_name)) {
                            SpHelper.setStringValue("USERNAME", employee_name);
                            SpHelper.setStringValue("PASSWORD", pwd);
                            if (ds_all_clean.isSwitchOn()) {
                           /* user_name.setText(sp.getString("username", ""));
                            password.setText(sp.getString("password", ""));*/
                                editor = sp.edit();
                                editor.putString("username", name);
                                editor.putString("password", pwd);
                                editor.putBoolean("isSwitchOn", true);
                                editor.commit();
                            }
                            List<String> trackXY = new ArrayList<>();
                            trackXY.addAll(xY());
                            if (trackXY.size() > 0) {
                                s.AddTrack(MainActivity.this, s.getUUID(), "", devinceNo, trackXY.get(0), trackXY.get(1),
                                        Constant.TRACK_EVENT_LOGIN, s.getCurrTime(), SpHelper.getStringValue("USERNAME"), "", Constant.GPS);
                            }
                            /**
                             * 压缩BO2,BO3文件
                             * 解压BO2,BO3文件
                             *
                             * */
                            List<String> alldir = new ArrayList<>();
                            alldir.addAll(FileUtil.getFilesAllName(FileUtil.encrypt + encryptDir));
                            zip4j = new CompressOperate_zip4j();
                            list = new ArrayList<>();
//                        for (int i = 0; i < alldir.size(); i++) {
////                    list.add(0,new File());
//////                    list.add(new File(alldir.get(i) + f));
//                            zip4j.compressZip4j(alldir.get(i) + FileUtil.bt, alldir.get(i), "123456");
//                            zip4j.compressZip4j(alldir.get(i) + FileUtil.bs, alldir.get(i), "123456");
//                        }
                            List<String> jiedir = new ArrayList<>();
                            jiedir.addAll(FileUtil.getFilesAllName(FileUtil.encrypt + encryptDir));
                            zip4j = new CompressOperate_zip4j();
                            list = new ArrayList<>();
                            list.add(new File(FileUtil.encrypt + encryptDir + FileUtil.encryptZIP));
                            FileUtil.deletefile(list);
                            list.clear();
                            for (int i = 0; i < jiedir.size(); i++) {
//                            list.add(new File(jiedir.get(i) + FileUtil.bt));
//                            list.add(new File(jiedir.get(i) + FileUtil.bs));
//                            FileUtil.deletefile(list);
//                            list.clear();
                                zip4j.uncompressZip4jUTF(jiedir.get(i) + FileUtil.bt + ".zip", jiedir.get(i), "123456");
                                zip4j.uncompressZip4jUTF(jiedir.get(i) + FileUtil.bs + ".zip", jiedir.get(i), "123456");
                                list.add(new File(jiedir.get(i) + FileUtil.bt + ".zip"));
                                list.add(new File(jiedir.get(i) + FileUtil.bs + ".zip"));
                                FileUtil.deletefile(list);
                            }
                            intent = new Intent(this, HomeActivity.class);
                            startActivity(intent);
                        } else {
                            ToastUtil.show(this, "用户名或密码错误");
                        }

                    } else {
                        ToastUtil.show(this, "用户名或密码不能为空");
                    }
                }

                break;
            default:
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!ds_all_clean.isSwitchOn()) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("isSwitchOn", false);
            editor.commit();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (!ds_all_clean.isSwitchOn()) {
            user_name.setText("");
            password.setText("");
        }

    }

    private void deleteFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                deleteFile(f);
            }
            //file.delete();//如要保留文件夹，只删除文件，请注释这行
        } else if (file.exists()) {
            file.delete();
        }
    }
}
