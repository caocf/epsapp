package com.epeisong.ui.activity;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.activity.XBaseActivity.OnChoosePictureListener;
import com.epeisong.base.adapter.EndlessAdapter;
import com.epeisong.base.adapter.EndlessAdapter.OnLoadMoreListener;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.base.view.AdjustHeightGridView;
import com.epeisong.base.view.AdjustHeightListView;
import com.epeisong.base.view.SlipButton;
import com.epeisong.base.view.SlipButton.SlipButtonChangeListener;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.DictionaryDao;
import com.epeisong.data.dao.UserDao;
import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.net.NetGuarantee;
import com.epeisong.data.net.parser.GuaranteeParser;
import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.common.Properties;
import com.epeisong.logistics.proto.Base.ProtoEGuaranteeProduct;
import com.epeisong.logistics.proto.Eps.GuaranteeReq;
import com.epeisong.logistics.proto.Eps.GuaranteeReq.Builder;
import com.epeisong.logistics.proto.Eps.GuaranteeResp;
import com.epeisong.model.Dictionary;
import com.epeisong.model.Guarantee;
import com.epeisong.model.User;
import com.epeisong.ui.fragment.HomeFragment.Item;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.AsyncTask;
import com.google.protobuf.ByteString;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
/**
 * 担保产品管理
 * @author Jack
 *
 */
public class ProductManaActivity extends BaseActivity implements OnItemClickListener, OnClickListener, SlipButtonChangeListener, OnChoosePictureListener, OnLoadMoreListener {
	//private static final int REQUEST_CODE_UPDATE_USER = 100;
	public static final int REQUEST_CODE_DETAIL = 101;

	private EditText tv_productmoney, tv_productinfor, tv_name;
	private TextView tv_type, tv_foruser;
	private int forgoodsType;
	private ImageView iv_img01, iv_img02;
	private int imgindex;
	//private String imgurl1, imgurl2;
	private Bitmap imgbmp1, imgbmp2;
	private int curposition;
	private User mUser;
	private int typeitem;
	private AdjustHeightListView lv;
	private TextView view_empty;
	MyAdapter mAdapter = new MyAdapter();
	MultiAdapter multiAdapter = new MultiAdapter();
	//protected EndlessAdapter mEndlessAdapter;

	private String[] areas;
	private boolean[] areaState;
	private ListView areaCheckListView;
	
	private Dialog userdialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		final List<Dictionary> RoleType = DictionaryDao.getInstance().queryByType(Dictionary.TYPE_ROLE);
		int size = RoleType.size();
		areas = new String[size];
		areaState = new boolean[size];
		for(int i=0; i<size;i++) {
			Dictionary dictionary = RoleType.get(i);
			areas[i] = RoleType.get(i).getName();
			areaState[i] = false; 
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_product_manage);
		mUser = UserDao.getInstance().getUser();
		TextView tvView = (TextView) findViewById(R.id.tv_publisher);
		tvView.setText(mUser.getShow_name());

		tv_type = (TextView) findViewById(R.id.tv_type);
		tv_foruser = (TextView) findViewById(R.id.tv_foruser);
		tv_productmoney = (EditText) findViewById(R.id.tv_productmoney);
		tv_productinfor = (EditText) findViewById(R.id.tv_productinfor);
		tv_name = (EditText) findViewById(R.id.tv_name);
		//tv_publisher.setOnClickListener(this);

		tv_type.setOnClickListener(this);
		//tv_foruser.setOnClickListener(new CheckBoxClickListener());
		tv_foruser.setOnClickListener(this);
		findViewById(R.id.btn_publish).setOnClickListener(this);

		iv_img01 = (ImageView) findViewById(R.id.iv_img01);
		iv_img01.setOnClickListener(this);
		iv_img02 = (ImageView) findViewById(R.id.iv_img02);
		iv_img02.setOnClickListener(this);

		lv = (AdjustHeightListView) findViewById(R.id.lv);
		lv.setOnItemClickListener(this);
		lv.setAdapter(mAdapter = new MyAdapter());

		if (mAdapter != null) {
			//mEndlessAdapter = new EndlessAdapter(this, mAdapter);
			//mEndlessAdapter.setIsAutoLoad(true);
			//mEndlessAdapter.setOnLoadMoreListener(this);
			//mEndlessAdapter.setHasMore(true);
			//lv.setAdapter(mEndlessAdapter);
		}
		view_empty = (TextView) findViewById(R.id.view_empty);
//		view_empty.setText(null);
//		lv.setEmptyView(view_empty);
		loadData(-1, "0", 0, true);
	}

	class CheckBoxClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			AlertDialog ad = new AlertDialog.Builder(ProductManaActivity.this)
			//.setTitle("选择货物类型")
			.setMultiChoiceItems(areas,areaState,new DialogInterface.OnMultiChoiceClickListener(){
				public void onClick(DialogInterface dialog,int whichButton, boolean isChecked){
					//点击某个区域
				}
			}).setPositiveButton("确定",new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog,int whichButton){
					String s = "";
					String stype = "";
					forgoodsType = 0;
					for (int i = 0; i < areas.length; i++){
						if (areaCheckListView.getCheckedItemPositions().get(i)){
							s += areaCheckListView.getAdapter().getItem(i)+ " ";
							//forgoodsType += 1<<i;
							stype = stype + i + ':';
							
						}else{
							areaCheckListView.getCheckedItemPositions().get(i,false);
						}
					}
					s=s.trim();

					if (areaCheckListView.getCheckedItemPositions().size() > 0){
						tv_foruser.setText(s);
						//Toast.makeText(AddQuoteActivity.this, s, Toast.LENGTH_LONG).show();
					}else{
						//没有选择
					}
					dialog.dismiss();
				}
			})
			//.setNegativeButton("取消", null)
			.create();
			areaCheckListView = ad.getListView();
			ad.setCanceledOnTouchOutside(true);
			ad.show();
		}
	}
	
	@Override
	public void onClick(final View v) {
		int id = v.getId();
		switch (id) {
		case R.id.iv_img01:
			imgindex = 1;
			choosePicture(false, this);
			break;
		case R.id.iv_img02:
			imgindex = 2;
			choosePicture(false, this);
			break;
		case R.id.tv_publisher:
			//simpleEdit(SimpleEditActivity.WHAT_CONTACTS_NAME, mUser.getContacts_name());
			break;
		case R.id.tv_foruser:
			final List<Dictionary> RoleType = DictionaryDao.getInstance().queryByType(Dictionary.TYPE_ROLE);
    		LayoutInflater factory = LayoutInflater.from(this);  
    		View view = factory.inflate(R.layout.dialog_usertype, null);  
    		AdjustHeightGridView mGridView;
    		 
    		mGridView = (AdjustHeightGridView) view.findViewById(R.id.gv_img);
    		Button bt_finish = (Button) view.findViewById(R.id.bt_finish);
    		bt_finish.setVisibility(View.VISIBLE);
    		bt_finish.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					String s = "";
					String stype = "";
					forgoodsType = 0;
					int selected = 0;
					int len = multiAdapter.getCount();
					
					for (int i = 0; i < len; i++){
						Dictionary dictionary = multiAdapter.getItem(i);
						if (areaState[i]){
							s += dictionary.getName() + " ";
							//forgoodsType += 1<<i;
							stype = stype + i + ':';
							selected++;
						}
					}
					s=s.trim();

					if (selected > 0){
						tv_foruser.setText(s);
						//Toast.makeText(AddQuoteActivity.this, s, Toast.LENGTH_LONG).show();
					}else{
						//没有选择
					}
					userdialog.dismiss();
				}
			});
    		mGridView.setNumColumns(2);
    		mGridView.setSelector(R.color.transparent);
    		mGridView.setAdapter(multiAdapter);
    		multiAdapter.addAll(RoleType);
    		//for(int i=0; i<arrayTypeName.length;i++)
    		//	mAdapter.addItem(new Item(arrayTypeName[i], 0).setUserTypeCode(arrayTypeCode[i]));
    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	builder.setView(view); 
        	userdialog = builder.show();
        	userdialog.setCanceledOnTouchOutside(true);//设置点击Dialog外部任意区域关闭Dialog 
			break;
		case R.id.tv_type:
//			typeitem = 1;
//			tv_type.setText("che");
			List<Dictionary> goodType=new ArrayList<Dictionary>(); 
			//= DictionaryDao.getInstance().queryByType(Dictionary.TYPE_GOODS_TYPE);
			Dictionary oneDictionary=new Dictionary();
			oneDictionary.setId(0);
			//"订车配货交易保障"
			oneDictionary.setName("订车配货保证金");//"车源货源保证金");//"固定金额保证金");
			oneDictionary.setType(Properties.GUARANTEE_PRODUCT_FIXED_AMOUNT_DEPOSIT);
			//oneDictionary.setSort_order(sort_order);
			
			goodType.add(oneDictionary);
			showDictionaryListDialog("选择担保产品类型", goodType, new OnChooseDictionaryListener() {
				@Override
				public void onChoosedDictionary(Dictionary item) {
					// ToastUtils.showToast(item.getName() + " - " +
					// item.getId());
					typeitem = item.getType();
					tv_type.setText(item.getName());
				}
			});
			break;
		case R.id.btn_publish:
			if(mUser.getUser_type_code()!= Properties.LOGISTIC_TYPE_GUARANTEE) {
				ToastUtils.showToast("不是担保角色");
				break;
			}
			String publisher = mUser.getShow_name();
			String typeString = tv_type.getText().toString();
			String namesString = tv_name.getText().toString();
			String acountString = tv_productmoney.getText().toString();
			String inforsString = tv_productinfor.getText().toString();
			if (!TextUtils.isEmpty(publisher) && imgbmp1!=null && imgbmp2!=null
				&& !TextUtils.isEmpty(typeString) && !TextUtils.isEmpty(namesString)
				&& !TextUtils.isEmpty(acountString) && !TextUtils.isEmpty(inforsString)) {
				final Guarantee d = new Guarantee();
				
				d.setId(mUser.getId());
				d.setPublisher(mUser.getShow_name());
				d.setGuaType(mUser.getUser_type_code());
				d.setAccount(Integer.valueOf(acountString)*100);
				d.setIntroduce(inforsString);
				d.setName(namesString);
				d.setType(typeitem);
				d.setTypeName(typeString);

		        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
		        	@Override
		        	protected Boolean doInBackground(Void... params) {
		        		NetGuarantee net = new NetGuarantee() {
		        			@Override
		        			protected int getCommandCode() {
		        				return CommandConstants.CREATE_GUARANTEE_PRODUCT_REQ;
		        			}
		        			@Override
		        			protected boolean onSetRequest(GuaranteeReq.Builder req) {
		        		    	//GuaranteeReq.Builder req = GuaranteeReq.newBuilder();
		        		    	ProtoEGuaranteeProduct.Builder builder = ProtoEGuaranteeProduct.newBuilder();
		        		    	builder.setGuaranteeId(Integer.valueOf(d.getId()));
		        		    	builder.setGuaranteeType(d.getGuaType());
		        		    	builder.setProductAmount(d.getAccount());
		        		    	builder.setProductName(d.getName());
		        		    	builder.setProductDesc(d.getIntroduce());
		        		    	builder.setGuaranteeName(d.GetPublisher());
		        		    	builder.setProductType(d.getType());
		        		    	builder.setStatus(Properties.GUARANTEE_PRODUCT_STATUS_NORMAL);
		        		    	
		        		    	
		        		        ByteArrayOutputStream out;
		        		        byte[] bytes;
		        		        ByteString bs;
		        		        out = new ByteArrayOutputStream();
		        		        imgbmp1.compress(CompressFormat.PNG, 100, out);
		        		        bytes = out.toByteArray();
		        		        bs = ByteString.copyFrom(bytes);
		        		        req.setOwnerLogo(bs);
		        		    	builder.setOwnerLogo(bs.toString());
		        		    	req.setLogoFileType(".png");
		        		    	
		        		        out = new ByteArrayOutputStream();
		        		        imgbmp2.compress(CompressFormat.PNG, 100, out);
		        		        bytes = out.toByteArray();
		        		        bs = ByteString.copyFrom(bytes);
		        		        req.setOtherLogo(bs);
		        		        builder.setOtherLogo(bs.toString());

		        		        req.setGuaranteeProduct(builder);
		        		    	req.setGuaranteeId(Integer.valueOf(d.getId()));

		        				return true;
		        			}
		        		};

		        		try {
		        			GuaranteeResp.Builder resp = net.request();
		        			if (resp == null) {
		        				return null;
		        			}
		        			return true;
		        		} catch (NetGetException e) {
		        			e.printStackTrace();
		        		}
		        		return null;                	

		        	}

		        	protected void onPostExecute(Boolean result) {
		        		if (result) {
		        			ToastUtils.showToast("发布成功");
		        			loadData(10, "0", 0, true);
		        		}
		        	}
		        };
		        task.execute();
				
				
				
//				NetPublishGuarantee net = new NetPublishGuarantee(this, d, imgbmp1, imgbmp2);
//				net.request(new OnNetRequestListenerImpl<GuaranteeResp.Builder>() {
//				
//					@Override
//					public void onFail(String msg) {
//						super.onFail(msg);
//					}
//					
//					@Override
//					public void onError() {
//						super.onError();
//					}
//					@Override
//					public void onSuccess(
//							GuaranteeResp.Builder response) {
//						// DispatchManager.getInstance().insert(d);
//						ToastUtils.showToast("发布成功");
//						loadData(10, "0", 0, true);
//					}
//				});

			} else {
				ToastUtils.showToast("请填写完整信息！");
			}
			break;
		}
	}

	@Override
	public void onStartLoadMore(EndlessAdapter adapter) {
		String edge_id = mAdapter.getItem(mAdapter.getCount() - 1).getId();
		//weightScore = mAdapter.getItem(mAdapter.getCount() - 1).getUserRole().getWeight();
		loadData(10, edge_id, 0, false);// remember to add
		// weight;
	}

	private void loadData(final int size, final String edge_id, final double weight, final boolean bFirst)
	{
		AsyncTask<Void, Void, List<Guarantee>> task = new AsyncTask<Void, Void, List<Guarantee>>() {

			@Override
			protected List<Guarantee> doInBackground(Void... params) {

				NetGuarantee netGuarantee = new NetGuarantee() {

					@Override
					protected int getCommandCode() {
						return CommandConstants.LIST_GUARANTEE_PRODUCT_REQ;
					}

					@Override
					protected boolean onSetRequest(Builder req) {
						req.setLimitCount(size);
                        int id = 0;
                        try {
                            if (edge_id != null) {
                                id = Integer.parseInt(edge_id);
                            }
                        } catch (Exception e) {
                            id = 0;
                        }
                        req.setStatus(0);//all
                        req.setId(id);
						req.setGuaranteeId(Integer.valueOf(mUser.getId()));
						req.setProductType(0);
						return true;
					}
				};
				try {
					com.epeisong.logistics.proto.Eps.GuaranteeResp.Builder resp = netGuarantee.request();
					//List<Guarantee> result = new ArrayList<Guarantee>();
					return GuaranteeParser.parse(resp);
				} catch (NetGetException e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(List<Guarantee> result) {
				if (result != null) {
					if (result.isEmpty()) {
						view_empty.setVisibility(View.VISIBLE);
						if (bFirst) {
							mAdapter.clear();
							
						} else {
						}
					} else {
						if (bFirst) {
							mAdapter.replaceAll(result);
							mAdapter.notifyDataSetChanged();
						} else {
							mAdapter.addAll(result);
						}
						view_empty.setVisibility(View.GONE);
					}
				} else {
					if (!bFirst) {
					} else
						mAdapter.clear();
					view_empty.setVisibility(View.VISIBLE);
				}

			}

		};
		task.execute();
	}

	@Override
	public void OnChanged(boolean CheckState, SlipButton btn) {
		Object tag = btn.getTag();
		if (tag != null && tag instanceof Guarantee) {
			changeState((Guarantee) tag, btn);
		}
	}

    private void changeState(final Guarantee f, final SlipButton btn) {
        final int curStatus = f.getStatus();

        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
        	@Override
        	protected Boolean doInBackground(Void... params) {
        		NetGuarantee net = new NetGuarantee() {
        			@Override
        			protected int getCommandCode() {
        				return CommandConstants.UPDATE_GUARANTEE_PRODUCT_STATUS_REQ;
        			}
        			@Override
        			protected boolean onSetRequest(GuaranteeReq.Builder req) {
        				req.setProductId(Integer.parseInt(f.getId()));
        				return true;
        			}
        		};

        		try {
        			GuaranteeResp.Builder resp = net.request();
        			if (resp == null) {
        				return null;
        			}
        			return true;
        		} catch (NetGetException e) {
        			e.printStackTrace();
        		}
        		return null;                	

        	}

        	protected void onPostExecute(Boolean result) {
        		if (result) {
                    if (curStatus == Properties.GUARANTEE_PRODUCT_STATUS_NORMAL) {
                        f.setStatus(Properties.GUARANTEE_PRODUCT_STATUS_INVALID);
                    } else {
                        f.setStatus(Properties.GUARANTEE_PRODUCT_STATUS_NORMAL);
                    }
                    mAdapter.notifyDataSetChanged();
        		}
        	}
        };
        task.execute();
        
        
//        NetGuaranteeUpdateStatus net = new NetGuaranteeUpdateStatus(this, f) {
//            @Override
//            protected int getCommandCode() {
//                return CommandConstants.UPDATE_GUARANTEE_PRODUCT_STATUS_REQ;
//            }
//            
//            @Override
//            protected boolean onSetRequest(GuaranteeReq.Builder req) {
//                req.setProductId(Integer.parseInt(f.getId()));
//                return true;
//            }
//        };
//        net.request(new OnNetRequestListenerImpl<Eps.GuaranteeResp.Builder>() {
//
//			@Override
//			public void onSuccess(Eps.GuaranteeResp.Builder response) {
//                if (curStatus == Properties.GUARANTEE_PRODUCT_STATUS_NORMAL) {
//                    f.setStatus(Properties.GUARANTEE_PRODUCT_STATUS_INVALID);
//                } else {
//                    f.setStatus(Properties.GUARANTEE_PRODUCT_STATUS_NORMAL);
//                }
//                mAdapter.notifyDataSetChanged();
//			}
//        	
//		});
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_DETAIL) {
    		int position=curposition;
    		position -= lv.getHeaderViewsCount();
    		Guarantee f = mAdapter.getItem(position);
            if (f.getStatus() == Properties.GUARANTEE_PRODUCT_STATUS_NORMAL) {
                f.setStatus(Properties.GUARANTEE_PRODUCT_STATUS_INVALID);
            } else {
                f.setStatus(Properties.GUARANTEE_PRODUCT_STATUS_NORMAL);
            }
    		mAdapter.notifyDataSetChanged();
    	}
    }
    
	protected TitleParams getTitleParams() {
		return new TitleParams(getDefaultHomeAction(), "担保产品管理", null).setShowLogo(false);
	}

//	private void simpleEdit(int extra_what, String extra_what_data) {
//		Intent intent = new Intent(this, SimpleEditActivity.class);
//		intent.putExtra(SimpleEditActivity.EXTRA_WHAT, extra_what);
//		intent.putExtra(SimpleEditActivity.EXTRA_WHAT_DATA, extra_what_data);
//		intent.putExtra("user", mUser);
//		startActivityForResult(intent, REQUEST_CODE_UPDATE_USER);
//	}

	private class MyAdapter extends HoldDataBaseAdapter<Guarantee> {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;
			if (convertView == null) {
				convertView = SystemUtils.inflate(R.layout.activity_guarantee_list);
				holder = new ViewHolder();
				holder.findView(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			Guarantee f = getItem(position);
			holder.fillData(f);

			if (f.getStatus() == Properties.GUARANTEE_PRODUCT_STATUS_NORMAL) {
				convertView.setBackgroundColor(Color.WHITE);
				convertView.setEnabled(true);
			} else {
				convertView.setBackgroundColor(Color.GRAY);
				convertView.setEnabled(false);
			}
			holder.enable(f.getStatus() == Properties.GUARANTEE_PRODUCT_STATUS_NORMAL);
			return convertView;
		}
	}

	private class ViewHolder {
		ImageView iv_guarantee;
		TextView tv_name;
		TextView tv_type, tv_money;
		//public SlipButton cb_switch;
		//ImageView iv_state;
	
        public void enable(boolean enabled) {
        	tv_name.setEnabled(enabled);
        	iv_guarantee.setEnabled(enabled);
            //if (!enabled) {
            //    iv_state.setImageResource(R.drawable.yiguoqi_icon);
            //}
        }
        
		public void fillData(Guarantee f) {
	        if (!TextUtils.isEmpty(f.getMark_url1())) {
	            ImageLoader.getInstance().displayImage(f.getMark_url1(), iv_guarantee);
	        }
			//iv_guarantee.setImageResource(R.drawable.productmoney);
			tv_name.setText(f.getName());
			if(f.getType()==0)
			{
				//订车配货交易保障"
				tv_type.setText("订车配货保证金");//车源货源保证金");//f.getTypeName());
			
			}
			tv_money.setText(String.valueOf(f.getAccount()/100)+"元");
//			if (f.getStatus() == Properties.GUARANTEE_PRODUCT_STATUS_NORMAL) {
//				cb_switch.setDefaultOpen(true);
//			} else {
//				cb_switch.setEnabled(false);
//				cb_switch.setDefaultOpen(false);
//			}
//			
//			cb_switch.setTag(f);
		}
	
		public void findView(View v) {
			iv_guarantee = (ImageView) v.findViewById(R.id.iv_guarantee);
			tv_name = (TextView) v.findViewById(R.id.tv_name);
			tv_type = (TextView) v.findViewById(R.id.tv_type);
			tv_type.setVisibility(View.GONE);
			tv_money = (TextView) v.findViewById(R.id.tv_money);
			v.findViewById(R.id.iv_switch).setVisibility(View.GONE);
			//iv_state = (ImageView) v.findViewById(R.id.iv_state);
			//cb_switch = (SlipButton) v.findViewById(R.id.iv_switch);
			//cb_switch.SetOnChangedListener(ProductManaActivity.this);
		}
	}

	private class MultiAdapter extends HoldDataBaseAdapter<Dictionary> {
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			mViewHolder holder = null;
			if (convertView == null) {
				convertView = SystemUtils.inflate(R.layout.fragment_multiuser_gridview_item);
				holder = new mViewHolder();
				holder.findView(convertView);
				convertView.setTag(holder);
			} else {
				holder = (mViewHolder) convertView.getTag();
			}
			holder.fillData(getItem(position));
			return convertView;
		}
	}

	private class mViewHolder {
		TextView tv_lstuser;
		LinearLayout ll_lstuser;
		CheckBox cb_lstuser;
		int typecode;
 
		public void findView(View v) {
			tv_lstuser = (TextView) v.findViewById(R.id.tv_lstuser);
			ll_lstuser = (LinearLayout) v.findViewById(R.id.ll_lstuser);
			cb_lstuser = (CheckBox) v.findViewById(R.id.cb_lstuser);

		}

		public void fillData(final Dictionary item) {
			tv_lstuser.setText(item.getName());
			cb_lstuser.setOnCheckedChangeListener( new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
					// TODO Auto-generated method stub
					areaState[item.getId()] = arg1;
				}
			});
			ll_lstuser.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					cb_lstuser.setChecked(!areaState[item.getId()]);
					//userdialog.dismiss();
				}
			});
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
		curposition=position;
		position -= lv.getHeaderViewsCount();
		Guarantee f = mAdapter.getItem(position);
		if (true) {
			Intent intent = new Intent(this, ProductDetailActivity.class);
			intent.putExtra(ProductDetailActivity.EXTRA_GUARANTEE, f);
			//intent.putExtra(ProductDetailActivity.EXTRA_CAN_DELETE, true);
			//intent.putExtra(ProductDetailActivity.EXTRA_USER_ID, mUser.getId());
			startActivityForResult(intent, REQUEST_CODE_DETAIL);
			return;
		}
	}

	@Override
	public void onChoosePicture(String path) {

//		BitmapFactory.Options options = new BitmapFactory.Options();
//		options.inSampleSize = 2;
//		Bitmap bm = BitmapFactory.decodeFile(path, options);
		Bitmap bm = BitmapFactory.decodeFile(path);
			
		if(imgindex==1) {
			imgbmp1 = bm;
			iv_img01.setImageBitmap(bm);
		} else {
			imgbmp2 = bm;
			iv_img02.setImageBitmap(bm);
		}
	}
	
}
