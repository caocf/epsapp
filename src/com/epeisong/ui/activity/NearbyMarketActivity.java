package com.epeisong.ui.activity;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import com.epeisong.utils.android.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.epeisong.base.adapter.EndlessAdapter;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.net.NetLogisticsSearch;
import com.epeisong.data.net.parser.UserParser;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.common.Properties;
import com.epeisong.model.Region;
import com.epeisong.model.RegionResult;
import com.epeisong.model.User;
import com.epeisong.model.UserRole;
import com.epeisong.ui.view.Choosable;
import com.epeisong.ui.view.Choosable.Choosion;
import com.epeisong.ui.view.ChooseServeRegionLayout;
import com.epeisong.ui.view.ChooseServeRegionLayout.OnChooseServeRegionListener;
import com.epeisong.utils.ToastUtils;

/**
 * 附近配货市场
 * 
 * @author gnn
 * 
 */
public class NearbyMarketActivity extends VerticalFilterActivity implements OnChooseServeRegionListener {

    public static final String EXTRA_DEFAULT_REGION = "default_region";

    private static final int LOAD_SIZE_FIRST = 10;
    private static final int LAOD_SIZE_MORE = 10;

    private int mStartRegionCode;
    private ChooseServeRegionLayout mChooseServeRegionLayout;
    private int Logistic_type = Properties.LOGISTIC_TYPE_MARKET;

    // 点击list列表进行跳转
    @Override
    public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
        // TODO
        if (position != 0) {
            User u = mAdapter.getItem(position - 1);
            Intent i = new Intent(this, FreightMarketDetailActivity.class);
            i.putExtra(FreightMarketDetailActivity.EXTRA_MARKET, u);
            startActivity(i);
        }
    }

    @Override
    protected TitleParams getTitleParams() {
        return new TitleParams(getDefaultHomeAction(), "逛逛配货市场", null).setShowLogo(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (mChooseServeRegionLayout.onActivityResult(requestCode, resultCode, data)) {
                return;
            }
        }
    }

    @Override
    public void onChoosedServeRegion(RegionResult result) {
        mStartRegionCode = result.getCode();
        setFilterValue(0, result.getShortNameFromDistrict());
        hideChoosableView(0);
        loadData(LOAD_SIZE_FIRST, "0", 0, true);
    }

    @Override
    public void onChoosedServeRegionDefault(Choosion choosion) {
        mStartRegionCode = 0;
        setFilterValue(0, choosion.getName());
        hideChoosableView(0);
        loadData(LOAD_SIZE_FIRST, "0", 0, true);
    }

    private void loadData(final int size, final String edge_id, final double weight, final boolean bFirst) {
        AsyncTask<Void, Void, List<User>> task = new AsyncTask<Void, Void, List<User>>() {
            @Override
            protected List<User> doInBackground(Void... params) {

                NetLogisticsSearch net = new NetLogisticsSearch() {
                    @Override
                    protected int getCommandCode() {
                        return CommandConstants.SEARCH_RecvGoods_ST_LU_LP_MARKET_REQ;
                    }

                    @Override
                    protected boolean onSetRequest(com.epeisong.logistics.proto.Eps.SearchCommonLogisticsReq.Builder req) {

                        req.setLimitCount(size);
                        int id = 0;
                        try {
                            if (edge_id != null) {
                                id = Integer.parseInt(edge_id);
                            }
                        } catch (Exception e) {
                            id = 0;
                        }
                        req.setId(id);
                        req.setLogisticTypeCode(Logistic_type);
                        req.setWeightScore(weight);
                        if (mStartRegionCode > 0) {
                            req.setServeRegionCode(mStartRegionCode);
                        }

                        return true;

                    }
                };
                try {
                    CommonLogisticsResp.Builder resp = net.request();
                    if (resp == null) {
                        return null;
                    }
                    return UserParser.parse(resp);
                } catch (NetGetException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<User> result) {
                dismissPendingDialog();
                if (result != null) {
                    if (result.isEmpty()) {
                        mEndlessAdapter.setHasMore(false);
                        if (bFirst) {
                            ToastUtils.showToast("没有数据");
                            mAdapter.clear();
                        } else {
                            mEndlessAdapter.endLoad(true);
                        }
                    } else {
                        mEndlessAdapter.setHasMore(result.size() >= size);
                        if (bFirst) {
                            mAdapter.replaceAll(result);
                        } else {
                            mAdapter.addAll(result);
                            mEndlessAdapter.endLoad(true);
                        }
                    }
                } else {
                    if (!bFirst) {
                        mEndlessAdapter.endLoad(false);
                    } else
                        mAdapter.clear();
                }
            }
        };
        task.execute();
        if (bFirst) {
            showPendingDialog(null);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO
        super.onCreate(savedInstanceState);
        hideSearchBtn();
        Region region = (Region) getIntent().getSerializableExtra(EXTRA_DEFAULT_REGION);
        if (region != null) {
            mStartRegionCode = region.getCode();
            setFilterValue(0, region.getName());
            loadData(LOAD_SIZE_FIRST, "0", 0, true);
        }
    }

    @Override
    protected Map<String, Choosable> onCreateFilterView() {

        Map<String, Choosable> map = new LinkedHashMap<String, Choosable>();
        map.put("选择城市", mChooseServeRegionLayout = new ChooseServeRegionLayout(getApplicationContext()));
        mChooseServeRegionLayout.setActivity(this);
        mChooseServeRegionLayout.setOnChooseServeRegionListener(this);
        mStartRegionCode = mChooseServeRegionLayout.getDefaultChoosion().getCode();
        return map;
    }

    @Override
    public void onStartLoadMore(EndlessAdapter adapter) {
        String edge_id = mAdapter.getItem(mAdapter.getCount() - 1).getId();
        UserRole userRole = mAdapter.getItem(mAdapter.getCount() - 1).getUserRole();
        double weight = 0;
        if (userRole != null) {
            weight = userRole.getWeight();
        }
        loadData(LAOD_SIZE_MORE, edge_id, weight, false);
    }

    @Override
    protected void onClickSearchBtn() {

    }
}
