package com.example.learningsqlite;

import android.app.Activity;
import android.app.DialogFragment;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.learningsqlite.AddMemberDialog.AddMemberDialogListener;
import com.example.learningsqlite.DatePickerInputDialog.NoticeDatePickerDialogListener;
import com.example.learningsqlite.ProductsContract.Products;
import com.example.learningsqlite.WorkersContract.Workers;

public class InputFormActivity extends Activity implements NoticeDatePickerDialogListener, AddMemberDialogListener {
	private static final String NEW_RECORD_ID = "New record id";
	private static final String REQUESTING_RECORD_ID = "requesting id";
//	private int[] inputIds;
	public static final String TAG = "InputFormActivity";
	public static final String CUSTOMER_ID = "customer ID";
	private DatabaseRecord record;
	private String TABLE_NAME;
	private String recId;
	private int layoutId;
	AddMemberDialog dialog;
	private int currentDateFieldId;
	public String addedComponentDomain;
	private LinearLayout custFrame;
	private LinearLayout productsSuperFrame;
	private LinearLayout workersSuperFrame;
//	private String newRecordId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
				
		switch (getIntent().getExtras().getString(ViewDbActivity.DOMAIN)) {
		case DbConstants.ORDERS:
			TABLE_NAME = OrdersContract.Orders.TABLE_NAME;
			layoutId = R.layout.activity_orders_new_record;
			if(getIntent().hasExtra(ViewDbActivity.RECORD_ID)){
				recId = getIntent().getExtras().getString(ViewDbActivity.RECORD_ID);
				record = new OrdersRecord(recId, this);
				setTitle(R.string.title_activity_order_edit);
			}else{
				setTitle(R.string.title_activity_orders_new_record);
				record = new OrdersRecord(this);
			}
			break;
		case DbConstants.CUSTOMERS:
			TABLE_NAME = CustomersContract.Customers.TABLE_NAME;
			layoutId = R.layout.layout_customer_input;

			if(getIntent().hasExtra(ViewDbActivity.RECORD_ID)){
				setTitle(R.string.title_activity_customer_edit);
				recId = getIntent().getExtras().getString(ViewDbActivity.RECORD_ID);
				record = new CustomersRecord(recId, this);
			}else{
				setTitle(R.string.title_activity_customers_new_record_form);
				record = new CustomersRecord(this);
			}
			break;
		case DbConstants.WORKERS:
			TABLE_NAME = Workers.TABLE_NAME;
			layoutId = R.layout.workers_input_form;
			if(getIntent().hasExtra(ViewDbActivity.RECORD_ID)){
				recId = getIntent().getExtras().getString(ViewDbActivity.RECORD_ID);
				record = new WorkersRecord(recId, this);
				setTitle(R.string.title_workers_edit);
			}else{
				setTitle(R.string.title_workers_new_record);
				record = new WorkersRecord(this);
			}
			break;
		case DbConstants.PRODUCTS:
			TABLE_NAME = Products.TABLE_NAME;
			layoutId = R.layout.products_input_form;
			if(getIntent().hasExtra(ViewDbActivity.RECORD_ID)){
				recId = getIntent().getExtras().getString(ViewDbActivity.RECORD_ID);
				record = new ProductsRecord(recId, this);
				setTitle(R.string.title_products_edit);
			}else{
				setTitle(R.string.title_products_new_record);
				record = new ProductsRecord(this);
			}
			break;
		default:
			break;
		}
		
		/*
		XMLLayoutParser parser = new XMLLayoutParser(this);
		
		try {
			// TODO take into account that date fields are buttons, not textViews
			inputIds = parser.getElementIds(layoutId, XMLLayoutParser.EDIT_TEXT);
		} catch (XmlPullParserException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}*/
		
		populateFields();
		if(TABLE_NAME.equals(Products.TABLE_NAME) )
			populatePriceUnitSpinner();
	

	}
	
	private ImageView getRemoveButton(){

		ImageView removeButton = new ImageView(this);
		LinearLayout.LayoutParams removalLayout = new LinearLayout.LayoutParams(60, 60);
		removalLayout.setMargins(0, 0, 12, 0);
		removalLayout.gravity = Gravity.RIGHT;
		removeButton.setLayoutParams(removalLayout);
		removeButton.setClickable(true);
		removeButton.setImageResource(R.drawable.red_x);
		return removeButton;
	}
	
	public void populateFields(){
		setContentView(layoutId);
		if(recId!=null){
			switch (TABLE_NAME) {
			// TODO factor out most of the code to uniform methods
			case OrdersContract.Orders.TABLE_NAME:
				//at the moment, no special code necessary
				OrdersRecord ordersRec = (OrdersRecord)record;
				ordersRec.populateInputFields(this);
				
				LinearLayout container = (LinearLayout)findViewById(R.id.orders_edit_form_container);
				// special code for handling methods associated with special nested nature of orders records
				if(ordersRec.customer!=null){
					custFrame = (LinearLayout)findViewById(R.id.orders_customer_fragment_container);
					getLayoutInflater().inflate(R.layout.layout_customer_input, custFrame);
					LinearLayout custHeader = new LinearLayout(this);
					custHeader.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
					custHeader.setOrientation(LinearLayout.HORIZONTAL);
					TextView frameTitle = new TextView(this);
					LinearLayout.LayoutParams titleLp =new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT);
					titleLp.weight = 1;
					frameTitle.setLayoutParams(titleLp);
					frameTitle.setText(R.string.label_customer_info);
					frameTitle.setTextSize(24);
					frameTitle.setTextColor(Color.BLUE);
					int addinIndex = container.indexOfChild(custFrame);
					custHeader.addView(frameTitle);
					Button toggler = getToggleButton();
					toggler.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							if(custFrame.getVisibility()==View.VISIBLE){
								custFrame.setVisibility(View.GONE);
								((Button)v).setText(R.string.btn_show_orders_member);
							}else{
								custFrame.setVisibility(View.VISIBLE);
								((Button)v).setText(R.string.btn_hide_orders_member);
							}
							
						}
					});
					custHeader.addView(toggler);
					container.addView(custHeader, addinIndex);
		//			custFrame.findViewById(R.id.customers_edit_form_button_bar).setVisibility(View.GONE);
					findViewById(R.id.btn_add_customer).setVisibility(View.GONE);
					ordersRec.customer.populateInputFields(this);
					ImageView removeButton = getRemoveButton();
					removeButton.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							((OrdersRecord)record).customer = null;
							populateFields();
						}
					});
					custFrame.addView(removeButton, 0);
					custFrame.setVisibility(View.GONE);
				}else{
					// inflate other fragment with add customer button
				}
				if(ordersRec.products.size()>0){
					productsSuperFrame = (LinearLayout)findViewById(R.id.orders_products_fragment_container);
					LinearLayout prodHeader = new LinearLayout(this);
					prodHeader.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
					prodHeader.setOrientation(LinearLayout.HORIZONTAL);
					TextView frameTitle = new TextView(this);
					LinearLayout.LayoutParams titleLp =new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT);
					titleLp.weight = 1;
					frameTitle.setLayoutParams(titleLp);					
					frameTitle.setText(R.string.label_product_info);
					frameTitle.setTextSize(24);
					frameTitle.setTextColor(Color.BLUE);
					int addinIndex = container.indexOfChild(productsSuperFrame);
					prodHeader.addView(frameTitle);
					Button toggler = getToggleButton();
					toggler.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							if(productsSuperFrame.getVisibility()==View.VISIBLE){
								productsSuperFrame.setVisibility(View.GONE);
								((Button)v).setText(R.string.btn_show_orders_member);
							}else{
								productsSuperFrame.setVisibility(View.VISIBLE);
								((Button)v).setText(R.string.btn_hide_orders_member);
							}
							
						}
					});
					prodHeader.addView(toggler);
					container.addView(prodHeader, addinIndex);
					for(int i = 0;i<ordersRec.products.size();i++){
						LinearLayout singleProductFrame = new LinearLayout(this);
						LayoutParams singleFrameLayParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
						singleProductFrame.setLayoutParams(singleFrameLayParams);
						singleProductFrame.setOrientation(LinearLayout.VERTICAL);
						productsSuperFrame.addView(singleProductFrame);
						getLayoutInflater().inflate(R.layout.products_input_form, singleProductFrame);
						singleProductFrame.findViewById(R.id.products_edit_form_button_bar).setVisibility(View.GONE);
						singleProductFrame.findViewById(R.id.products_amount_container).setVisibility(View.VISIBLE);
						ordersRec.products.get(i).populateInputFields(singleProductFrame);
						ImageView removeButton = getRemoveButton();
						removeButton.setOnClickListener(new View.OnClickListener() {
							
							@Override
							public void onClick(View v) {
								Integer listIndex = (Integer) v.getTag();
								((OrdersRecord)record).products.remove(listIndex.intValue());
								populateFields();
								
							}
						});
						removeButton.setTag(Integer.valueOf(i));
						singleProductFrame.addView(removeButton, 0);
					}
					productsSuperFrame.setVisibility(View.GONE);
				}else{
					// inflate other fragment with add product button
				}
				if(ordersRec.workers.size()>0){
					workersSuperFrame = (LinearLayout)findViewById(R.id.orders_workers_fragment_container);
					LinearLayout workHeader = new LinearLayout(this);
					workHeader.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
					workHeader.setOrientation(LinearLayout.HORIZONTAL);
					TextView frameTitle = new TextView(this);
					LinearLayout.LayoutParams titleLp =new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT);
					titleLp.weight = 1;
					frameTitle.setLayoutParams(titleLp);
					frameTitle.setText(R.string.label_workers_info);
					frameTitle.setTextSize(24);
					frameTitle.setTextColor(Color.BLUE);
					int addinIndex = container.indexOfChild(workersSuperFrame);
					workHeader.addView(frameTitle);
					Button toggler = getToggleButton();
					toggler.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							if(workersSuperFrame.getVisibility()==View.VISIBLE){
								workersSuperFrame.setVisibility(View.GONE);
								((Button)v).setText(R.string.btn_show_orders_member);
							}else{
								workersSuperFrame.setVisibility(View.VISIBLE);
								((Button)v).setText(R.string.btn_hide_orders_member);
							}
							
						}
					});
					workHeader.addView(toggler);
					container.addView(workHeader, addinIndex);
					for(int i = 0;i<ordersRec.workers.size();i++){
						LinearLayout singleWorkerFrame = new LinearLayout(this);
						LayoutParams singleFrameLayParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
						singleWorkerFrame.setLayoutParams(singleFrameLayParams);
						singleWorkerFrame.setOrientation(LinearLayout.VERTICAL);
						workersSuperFrame.addView(singleWorkerFrame);
						getLayoutInflater().inflate(R.layout.workers_input_form, singleWorkerFrame);
						singleWorkerFrame.findViewById(R.id.workers_edit_form_button_bar).setVisibility(View.GONE);
						ordersRec.workers.get(i).populateInputFields(singleWorkerFrame);
						ImageView removeButton = getRemoveButton();
						removeButton.setOnClickListener(new View.OnClickListener() {
							
							@Override
							public void onClick(View v) {
								Integer listIndex = (Integer) v.getTag();
								((OrdersRecord)record).workers.remove(listIndex.intValue());
								populateFields();
								
							}
						});
						removeButton.setTag(Integer.valueOf(i));
						singleWorkerFrame.addView(removeButton, 0);
					}
					workersSuperFrame.setVisibility(View.GONE);
				}else{
					// inflate other fragment with add workers button
				}
				break;
			case "Bids":
				// ditto
				break;

			default:
				record.populateInputFields(this);
				break;
			}
			
		}else{
			record.populateInputFields(this);
		}
	}
	
	public void addSaleUnit(View v){
		Spinner sp = (Spinner)findViewById(R.id.products_input_sale_unit_spinner);
		EditText saleUnitInput = (EditText)findViewById(R.id.products_input_new_sale_unit);
		if(sp.getVisibility()==View.VISIBLE){
			sp.setVisibility(View.GONE);
			sp.setSelected(false);
			saleUnitInput.setVisibility(View.VISIBLE);
		}else{
			saleUnitInput.setVisibility(View.GONE);
			sp.setVisibility(View.VISIBLE);
			sp.setSelected(true);
			sp.setSelection(0);
		}
	}
	
	private Button getToggleButton() {
		Button toggler = new Button(this);
		LinearLayout.LayoutParams togglerLayout = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	//	togglerLayout.gravity = Gravity.RIGHT;
		toggler.setLayoutParams(togglerLayout);
		toggler.setText(R.string.btn_show_orders_member);
		toggler.setTextSize(22);
		return toggler;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
//		getMenuInflater().inflate(R.menu.orders_new_record_form, menu);
		return false;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void addRecordMember(View v){
		// turn it into a fragment when adding cust/prod/worker from within other domain
		
		
		addedComponentDomain = (String) v.getTag();
		dialog = new AddMemberDialog();
		dialog.show(getFragmentManager(), "dialog " + addedComponentDomain);
		
	}
	
	
	// 'save' button click method -- for saving inputed data to database as a new record (or updating) 
	public void saveNew(View view) throws Exception {
		
		Log.d(TAG, "table: "+TABLE_NAME);
		if(record.recordId==null){
			
			if(record instanceof OrdersRecord){
				OrdersRecord ordRec = (OrdersRecord)record;
				ordRec.saveDataToObject(this);
			}else
				record.saveDataToObject(this);
			
			if(record.insertNewRecord()){ // if values were typed into fields
				Toast.makeText(this, getString(R.string.record_saved_success_message), Toast.LENGTH_SHORT).show();
	/*			if(getIntent().hasExtra(REQUESTING_RECORD_ID)){
					Intent intent = new Intent(this, InputFormActivity.class);
					// send back id of record that the request came from 
					intent.putExtra(ViewDbActivity.RECORD_ID, getIntent().getStringExtra(REQUESTING_RECORD_ID));
					// send id of new 'child' record that was just created
					intent.putExtra(NEW_RECORD_ID, record.recordId);
					startActivity(intent);
					
				}
				else{*/
					if(record.db.isOpen())
						record.db.close();
					if(record.dbHelper!=null)
						record.dbHelper.close();
					finish();
			//	}
			}else{
				Toast.makeText(this, getString(R.string.record_saved_failure_message), Toast.LENGTH_SHORT).show();
			//	Toast.makeText(this, "Empty record", Toast.LENGTH_SHORT).show();
				finish();
			}

		}else{ 
			record.saveDataToObject(this);
			if(record.updateRecord(record.valueMap)){
				Toast.makeText(this, getString(R.string.record_update_success_message), Toast.LENGTH_SHORT).show();	
			}else{
				Toast.makeText(this, getString(R.string.record_update_failure_message), Toast.LENGTH_SHORT).show();	
			}
			finish();
		}
	}
	
	private void populatePriceUnitSpinner(){
		Cursor c = record.db.query(Products.TABLE_NAME, null, 
				null, null, Products.COLUMN_NAME_SELL_BY_UNIT, null, null); // new String[]{Products.COLUMN_NAME_SELL_BY_UNIT} 
		SimpleCursorAdapter priceUnitAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, c, new String[]{Products.COLUMN_NAME_SELL_BY_UNIT}, new int[]{android.R.id.text1}, 0);
		priceUnitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner spinner = (Spinner)findViewById(R.id.products_input_sale_unit_spinner);
		spinner.setAdapter(priceUnitAdapter);
	}
	
	
	public void cancel(View view){
		finish();
	}
	
	@Override
	protected void onDestroy() {
		
		super.onDestroy();
	}

	@Override
	protected void onStop() {
		if(record!=null && record.db.isOpen())
			record.db.close();
//		if(record!=null)
//			record.dbHelper.close();
		super.onStop();
	}
	
	public void openDatePicker(View v){
		if(v.getId()==R.id.orders_order_date_btn)
			currentDateFieldId = R.id.orders_new_record_order_date_field;
		else if(v.getId()==R.id.orders_target_date_btn)
			currentDateFieldId = R.id.orders_new_record_fill_by_date_field;
		DatePickerInputDialog dp = new DatePickerInputDialog();
		dp.show(getFragmentManager(), DbConstants.DATE_PICKER_DIALOG);
		
	}

	@Override
	public void onDateDialogPositiveClick(DialogFragment dialog) {
		int[] dateVals = DbConstants.lastPickedDate;
		EditText dateField = (EditText)findViewById(currentDateFieldId);
		dateField.setText(Integer.toString(dateVals[0])+"."+Integer.toString(dateVals[1])+"."+Integer.toString(dateVals[2]));
	}

	@Override
	public void onDateDialogNegativeClick(DialogFragment dialog) {
		//closes dialog, no date value saved
		
	}

	@Override
	public void onAddMemberAcceptClickListener() {
	//	Toast.makeText(this, "record id: " + dialog.addedMemberId, Toast.LENGTH_SHORT).show();
		switch (addedComponentDomain) {
		case "customers":
			CustomersRecord cust = new CustomersRecord(dialog.addedMemberId, this);
			((OrdersRecord)record).customer = cust;
			break;
		case "products":
			ProductsRecord prod = new ProductsRecord(dialog.addedMemberId,this);
			((OrdersRecord)record).products.add(prod);
			break;
		case "workers":
			WorkersRecord work = new WorkersRecord(dialog.addedMemberId, this);
			((OrdersRecord)record).workers.add(work);
			break;
		}
		
		((OrdersRecord)record).saveBasicFieldsToObject(this);
		populateFields();
		addedComponentDomain = null;
	}

	@Override
	public void onAddMemberNewClickListener() {
		// TODO Auto-generated method stub
		Toast.makeText(this, "New member requested", Toast.LENGTH_SHORT).show();
	}

}
