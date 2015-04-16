package com.epeisong.ui.activity.temp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.DictionaryDao;
import com.epeisong.logistics.common.Properties;
import com.epeisong.model.Dictionary;
import com.epeisong.model.RegionResult;
import com.epeisong.ui.activity.user.TempActivity;
import com.epeisong.ui.view.SwitchButton;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;

/**
 * 选择角色
 * 
 * @author poet
 * 
 */
public class ChooseRoleActivity extends TempActivity implements OnItemClickListener, OnClickListener {

    public static SetupModel sSetupModel;

    private GridView mGridView;
    private MyAdapter mAdapter;
    private TextView mChoosedRoleTv;
    private SwitchButton mSwitchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_role);
        mGridView = (GridView) findViewById(R.id.gridview);
        mGridView.setAdapter(mAdapter = new MyAdapter());
        mGridView.setOnItemClickListener(this);
        mChoosedRoleTv = (TextView) findViewById(R.id.tv_role_name);
        mSwitchButton = (SwitchButton) findViewById(R.id.switchBtn);
        mSwitchButton.setSwitchText("公开", "不公开", true);
        mSwitchButton.setSwitchTextSize(16);
        findViewById(R.id.btn_next).setOnClickListener(this);

        List<Dictionary> dicts = DictionaryDao.getInstance().queryByType(Dictionary.TYPE_ROLE);
        List<Role> roles = new ArrayList<ChooseRoleActivity.Role>();
        for (Dictionary d : dicts) {
            Role role = new Role();
            role.setCode(d.getId());
            role.setName(d.getName());
            roles.add(role);
        }
        mAdapter.replaceAll(roles);

        sSetupModel = new SetupModel();
    }

    @Override
    protected TitleParams getTitleParams() {
        return new TitleParams(getDefaultHomeAction(), "选择角色", null).setShowLogo(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_next:
            if (sSetupModel.getRole() == null) {
                ToastUtils.showToast("请选择一个角色类型~");
                return;
            }
            sSetupModel.setIsHide(mSwitchButton.isOpen() ? Properties.LOGISTIC_NOT_HIDE : Properties.LOGISTIC_HIDE);
            Intent intent = new Intent(this, SetupAccountInfoActivity.class);
            startActivity(intent);
            break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Role role = mAdapter.getItem(position);
        if (role.isChecked()) {
            role.setChecked(false);
            sSetupModel.setRole(null);
        } else {
            for (Role r : mAdapter.getAllItem()) {
                r.setChecked(false);
            }
            role.setChecked(true);
            sSetupModel.setRole(role);
        }
        mAdapter.notifyDataSetChanged();
        if (sSetupModel.getRole() == null) {
            mChoosedRoleTv.setText("");
        } else {
            mChoosedRoleTv.setText(sSetupModel.getRole().getName());
        }
    }

    private class MyAdapter extends HoldDataBaseAdapter<Role> {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = SystemUtils.inflate(R.layout.activity_choose_role_item);
                holder = new ViewHolder();
                holder.findView(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.fillData(getItem(position));
            return convertView;
        }
    }

    private class ViewHolder {
        ImageView iv_role_icon;
        View cb;
        TextView tv_role_name;

        public void findView(View v) {
            iv_role_icon = (ImageView) v.findViewById(R.id.iv_role_icon);
            cb = v.findViewById(R.id.cb);
            tv_role_name = (TextView) v.findViewById(R.id.tv_role_name);
        }

        public void fillData(Role role) {
            iv_role_icon.setImageResource(role.getResId());
            cb.setSelected(role.isChecked());
            tv_role_name.setText(role.getName());
        }
    }

    public static class Role implements Serializable {

        private static final long serialVersionUID = 4245351521342607613L;

        private int code;
        private String name;
        private int resId;
        private boolean checked;

        public int getCode() {
            return code;
        }

        public Role setCode(int code) {
            this.code = code;
            return this;
        }

        public String getName() {
            return name;
        }

        public Role setName(String name) {
            this.name = name;
            return this;
        }

        public int getResId() {
            return resId;
        }

        public Role setResId(int resId) {
            this.resId = resId;
            return this;
        }

        public boolean isChecked() {
            return checked;
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
        }

    }

    /**
     * 角色设置模型
     * 
     * @author poet
     * 
     */
    public static class SetupModel implements Serializable {

        private static final long serialVersionUID = -6922575406105773824L;

        private Role role;
        private int isHide;
        private String showName;
        private RegionResult userRegion;
        private String userRegionInfo;
        private String contactsName;
        private String contactsPhone;
        private String contactsTelephone;
        private RegionResult roleRegion;
        private RegionResult roleLineStart;
        private RegionResult roleLineEnd;
        private Map<Integer, Dictionary> roleAttrMap;
        private String roleIntro;

        public Role getRole() {
            return role;
        }

        public void setRole(Role role) {
            this.role = role;
        }

        public int getIsHide() {
            return isHide;
        }

        public void setIsHide(int isHide) {
            this.isHide = isHide;
        }

        public String getShowName() {
            return showName;
        }

        public void setShowName(String showName) {
            this.showName = showName;
        }

        public RegionResult getUserRegion() {
            return userRegion;
        }

        public void setUserRegion(RegionResult userRegion) {
            this.userRegion = userRegion;
        }

        public String getUserRegionInfo() {
            return userRegionInfo;
        }

        public void setUserRegionInfo(String userRegionInfo) {
            this.userRegionInfo = userRegionInfo;
        }

        public String getContactsName() {
            return contactsName;
        }

        public void setContactsName(String contactsName) {
            this.contactsName = contactsName;
        }

        public String getContactsPhone() {
            return contactsPhone;
        }

        public void setContactsPhone(String contactsPhone) {
            this.contactsPhone = contactsPhone;
        }

        public String getContactsTelephone() {
            return contactsTelephone;
        }

        public void setContactsTelephone(String contactsTelephone) {
            this.contactsTelephone = contactsTelephone;
        }

        public RegionResult getRoleRegion() {
            return roleRegion;
        }

        public void setRoleRegion(RegionResult roleRegion) {
            this.roleRegion = roleRegion;
        }

        public RegionResult getRoleLineStart() {
            return roleLineStart;
        }

        public void setRoleLineStart(RegionResult roleLineStart) {
            this.roleLineStart = roleLineStart;
        }

        public RegionResult getRoleLineEnd() {
            return roleLineEnd;
        }

        public void setRoleLineEnd(RegionResult roleLineEnd) {
            this.roleLineEnd = roleLineEnd;
        }

        @SuppressLint("UseSparseArrays")
        public Map<Integer, Dictionary> getRoleAttrMap() {
            if (roleAttrMap == null) {
                roleAttrMap = new HashMap<Integer, Dictionary>();
            }
            return roleAttrMap;
        }

        public String getRoleIntro() {
            return roleIntro;
        }

        public void setRoleIntro(String roleIntro) {
            this.roleIntro = roleIntro;
        }

    }
}
