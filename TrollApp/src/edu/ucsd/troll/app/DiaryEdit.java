package edu.ucsd.troll.app;

import android.app.Activity;
import android.app.ListActivity;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class DiaryEdit extends Activity{

	private EditText mTitleText;
	private EditText mBodyText;
	private EditText mNumShots;
    private EditText mOzCoffee;
	private Long mRowId;
	private NotesDbAdapter mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
        setContentView(R.layout.note_edit);
        setTitle(R.string.diary_edit_note);
        
        mTitleText = (EditText) findViewById(R.id.diary_title);
        mBodyText  = (EditText) findViewById(R.id.diary_body);
        mNumShots  = (EditText) findViewById(R.id.num_espresso_shots);
        mOzCoffee  = (EditText) findViewById(R.id.oz_coffee);
        
        
//        Spinner coffee_spinner = (Spinner) findViewById(R.id.coffee_sizes_spinner);
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,  
//        		R.array.coffee_sizes, android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        coffee_spinner.setAdapter(adapter);

		final Button confirmButton = (Button) findViewById(R.id.diary_confirm);
		//        final Button deleteButton  = (Button) findViewById(R.id.diary_deleteBtn);

		mRowId = (savedInstanceState == null) ? null :
			(Long) savedInstanceState.getSerializable(NotesDbAdapter.KEY_ROWID);
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras.getLong(NotesDbAdapter.KEY_ROWID)
					: null;
		}
		Typeface btn_font = Typeface.createFromAsset(getAssets(), "KaushanScript-Regular.ttf");
		confirmButton.setTypeface(btn_font);
		//        deleteButton.setTypeface(btn_font);

		// Set Button Shape
		Drawable round_btn = getResources().getDrawable(R.drawable.round_btn);
		confirmButton.setBackgroundDrawable(round_btn);
		//        deleteButton.setBackgroundDrawable(round_btn);

		populateFields();

		confirmButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				setResult(RESULT_OK);
				finish();
			}
		});

		//       	deleteButton.setOnClickListener(new View.OnClickListener() {
		//
		//			@Override
		//			public void onClick(View v) {
		//				if(mDbHelper != null){
		//					mDbHelper.deleteNote(R.id.diary_deleteBtn);
		//					//fillData();
		//				}
		//			}
		//		});
	}

	@SuppressWarnings("deprecation")
	private void populateFields() {
        if (mRowId != null) {
            Cursor note = mDbHelper.fetchNote(mRowId);
            startManagingCursor(note);
            mTitleText.setText(note.getString(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE)));
            mBodyText.setText(note.getString(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY)));
            mNumShots.setText(note.getString(
            		note.getColumnIndexOrThrow(NotesDbAdapter.KEY_NUM_SHOTS)));
            mOzCoffee.setText(note.getString(
            		note.getColumnIndexOrThrow(NotesDbAdapter.KEY_SIZE_CUP)));
//            mSpinnerVal = note.getInt(
//            		note.getColumnIndexOrThrow(NotesDbAdapter.KEY_SIZE_CUP));
        }
    }

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveState();
		outState.putSerializable(NotesDbAdapter.KEY_ROWID, mRowId);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if(mDbHelper != null){
			saveState();
			mDbHelper.close();
			mDbHelper = null;
		}
	}

    @Override
    protected void onResume() {
        super.onResume();
        populateFields();
        if(mDbHelper == null){
        	mDbHelper.open();
        }
    }

    private void saveState() {
    	if(mDbHelper != null & mTitleText != null
    			 & mBodyText != null ){
	        String title = mTitleText.getText().toString();
	        String body  = mBodyText.getText().toString();
	        int num_shots = Integer.parseInt(mNumShots.getText().toString());
	        int oz_coffee = Integer.parseInt(mOzCoffee.getText().toString());
	        if (mRowId == null) {
	            long id = mDbHelper.createNote(title, body, num_shots, oz_coffee);
	            if (id > 0) {
	                mRowId = id;
	            }
	        } else {
	            mDbHelper.updateNote(mRowId, title, body, num_shots, oz_coffee);
	        }
    	}
    }
    
//    public void onItemSelected(AdapterView<?> parent, View view, 
//            int pos, long id) {
//        // An item was selected. You can retrieve the selected item using
//        // parent.getItemAtPosition(pos)
//    	
//    	mSpinnerVal = pos;
//    	
//    	/* Coffee values are as follows:
//    	 * 0 - [No Coffee]
//         * 1 - Extra Small (8 Oz)
//         * 2 - Small (12 Oz)
//       	 * 3 - Medium (16 Oz)
//         * 4 - Large (20 Oz)
//         * 5 - Extra Large (24+ Oz)
//    	 */
//    }
//
//    public void onNothingSelected(AdapterView<?> parent) {
//        // Another interface callback
//    }

}
