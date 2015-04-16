package com.epeisong.ui.activity;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.UserDao;
import com.epeisong.logistics.common.Properties;
import com.epeisong.model.User;

/**
 * 我的报价
 * 
 * @author jack
 * 
 */
public class MineQuoteActivity extends MenuListActivity {
	private int logitics_type;
	private int long_distance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long_distance = 1;

//		default:
//			TextView tv = new TextView(this);
//	        tv.setGravity(Gravity.CENTER);
//	        tv.setText("报价功能即将上线，敬请期待");
//	        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
//	        setContentView(tv);
//			break;
//		}
    }

    @Override
    protected TitleParams getTitleParams() {
        return new TitleParams(getDefaultHomeAction(), "我的报价").setShowLogo(false);
    }

	@Override
	protected void onSetData(final List<Menu> data) {
		User user = UserDao.getInstance().getUser();
		if (user == null) {
			return;
		}
		logitics_type = user.getUser_type_code();

		switch (logitics_type) {
		case Properties.LOGISTIC_TYPE_ENTIRE_VEHICLE:
		case Properties.LOGISTIC_TYPE_LESS_THAN_TRUCKLOAD_AND_LINE:
		case Properties.LOGISTIC_TYPE_LOAD_UNLOAD:
			data.add(new Menu(Menu.TYPE_INVALID, 0, false, 1, "", null));
			data.add(new Menu(Menu.TYPE_NOICON, 0, true, 1, "添加报价", new Runnable() {

				@Override
				public void run() {
					//long_distance = 1;
					Intent intent = new Intent(getApplicationContext(), AddQuoteActivity.class);
					//intent.putExtra("longdis", long_distance);
					
					intent.putExtra("logiticstype", logitics_type);
					startActivity(intent);
				}
			}));
			break;
		case Properties.LOGISTIC_TYPE_TRANSHIP_GOODS:
		case Properties.LOGISTIC_TYPE_EXPRESS:
			data.add(new Menu(Menu.TYPE_INVALID, 0, false, 1, "", null));
			final String string1;
			if(Properties.LOGISTIC_TYPE_TRANSHIP_GOODS == logitics_type)
				string1 = "添加报价(异地驳货)";
			else {
				string1 = "添加报价(异地快递)";
			}
			data.add(new Menu(Menu.TYPE_NOICON, 0, true, 1, string1, new Runnable() {

				@Override
				public void run() {
					//long_distance = 1;
					Intent intent = new Intent(getApplicationContext(), AddQuoteActivity.class);
					//intent.putExtra("longdis", long_distance);
					intent.putExtra("quotename", string1);
					intent.putExtra("logiticstype", logitics_type);
					startActivity(intent);
				}
			}));

			final String string2;
			if(Properties.LOGISTIC_TYPE_TRANSHIP_GOODS == logitics_type)
				string2 = "添加报价(同城驳货)";
			else {
				string2 = "添加报价(同城配送)";
			}
			data.add(new Menu(Menu.TYPE_NOICON, 0, true, 1, string2, new Runnable() {

				@Override
				public void run() {
					//long_distance = 2;
					Intent intent = new Intent(getApplicationContext(), AddQuoteActivity.class);
					intent.putExtra("longdis", 2);//long_distance);
					intent.putExtra("quotename", string2);
					intent.putExtra("logiticstype", logitics_type);
					startActivity(intent);
				}
			}));
			break;
		default:
			if(long_distance==-1)
				break;
			data.add(new Menu(Menu.TYPE_INVALID, 0, false, 1, "", null));
			data.add(new Menu(Menu.TYPE_NOICON, 0, true, 1, "添加报价(整车)", new Runnable() {

				@Override
				public void run() {
					//long_distance = 1;
					Intent intent = new Intent(getApplicationContext(), AddQuoteActivity.class);
					//intent.putExtra("longdis", long_distance);
					intent.putExtra("logiticstype", Properties.LOGISTIC_TYPE_ENTIRE_VEHICLE);
					startActivity(intent);
				}
			}));
			
			data.add(new Menu(Menu.TYPE_INVALID, 0, false, 1, "", null));
			data.add(new Menu(Menu.TYPE_NOICON, 0, true, 1, "添加报价(异地快递)", new Runnable() {

				@Override
				public void run() {
					//long_distance = 1;
					Intent intent = new Intent(getApplicationContext(), AddQuoteActivity.class);
					//intent.putExtra("longdis", long_distance);
					intent.putExtra("quotename", "添加报价(异地快递)");
					intent.putExtra("logiticstype", Properties.LOGISTIC_TYPE_EXPRESS);
					startActivity(intent);
				}
			}));
			data.add(new Menu(Menu.TYPE_NOICON, 0, true, 1, "添加报价(同城配送)", new Runnable() {

				@Override
				public void run() {
					//long_distance = 2;
					Intent intent = new Intent(getApplicationContext(), AddQuoteActivity.class);
					intent.putExtra("longdis", 2);//long_distance);
					intent.putExtra("quotename", "添加报价(同城配送)");
					intent.putExtra("logiticstype", Properties.LOGISTIC_TYPE_EXPRESS);
					startActivity(intent);
				}
			}));
			
			data.add(new Menu(Menu.TYPE_INVALID, 0, false, 1, "", null));
			data.add(new Menu(Menu.TYPE_NOICON, 0, true, 1, "添加报价(异地驳货)", new Runnable() {

				@Override
				public void run() {
					//long_distance = 1;
					Intent intent = new Intent(getApplicationContext(), AddQuoteActivity.class);
					//intent.putExtra("longdis", long_distance);
					intent.putExtra("quotename", "添加报价(异地驳货)");
					intent.putExtra("logiticstype", Properties.LOGISTIC_TYPE_TRANSHIP_GOODS);
					startActivity(intent);
				}
			}));
			data.add(new Menu(Menu.TYPE_NOICON, 0, true, 1, "添加报价(同城驳货)", new Runnable() {

				@Override
				public void run() {
					//long_distance = 2;
					Intent intent = new Intent(getApplicationContext(), AddQuoteActivity.class);
					intent.putExtra("longdis", 2);//long_distance);
					intent.putExtra("quotename", "添加报价(同城驳货)");
					intent.putExtra("logiticstype", Properties.LOGISTIC_TYPE_TRANSHIP_GOODS);
					startActivity(intent);
				}
			}));

			data.add(new Menu(Menu.TYPE_INVALID, 0, false, 1, "", null));
			data.add(new Menu(Menu.TYPE_NOICON, 0, true, 1, "添加报价(专线)", new Runnable() {

				@Override
				public void run() {
					//long_distance = 1;
					Intent intent = new Intent(getApplicationContext(), AddQuoteActivity.class);
					//intent.putExtra("longdis", long_distance);
					intent.putExtra("logiticstype", Properties.LOGISTIC_TYPE_LESS_THAN_TRUCKLOAD_AND_LINE);
					startActivity(intent);
				}
			}));
			data.add(new Menu(Menu.TYPE_INVALID, 0, false, 1, "", null));
			data.add(new Menu(Menu.TYPE_NOICON, 0, true, 1, "添加报价(装卸)", new Runnable() {

				@Override
				public void run() {
					//long_distance = 1;
					Intent intent = new Intent(getApplicationContext(), AddQuoteActivity.class);
					//intent.putExtra("longdis", long_distance);
					intent.putExtra("logiticstype", Properties.LOGISTIC_TYPE_LOAD_UNLOAD);
					startActivity(intent);
				}
			}));

			break;

		}

		data.add(new Menu(Menu.TYPE_INVALID, 0, false, 1, "", null));
		data.add(new Menu(Menu.TYPE_NOICON, 0, true, 1, "报价管理", new Runnable() {

			@Override
			public void run() {
				//Intent intent = new Intent(getApplicationContext(), MinetoOtherActivity.class);
				//startActivity(intent);
			}
		}));

	}

//    protected void onSetData(List<Menu> data) {
//
//        if (true) {
//            return;
//        }
//
//        data.add(new Menu(Menu.TYPE_INVALID, 0, false, 1, "", null));
//        data.add(new Menu(Menu.TYPE_NOICON, 0, true, 1, "别人给我的报价", new Runnable() {
//
//            @Override
//            public void run() {
//                Intent intent = new Intent(getApplicationContext(), MinetoMeActivity.class);
//                startActivity(intent);
//            }
//        }));
//
//        data.add(new Menu(Menu.TYPE_INVALID, 0, false, 1, "", null));
//        data.add(new Menu(Menu.TYPE_NOICON, 0, true, 1, "我给别人的报价", new Runnable() {
//
//            @Override
//            public void run() {
//                Intent intent = new Intent(getApplicationContext(), MinetoOtherActivity.class);
//                startActivity(intent);
//            }
//        }));
//
//    }
}
