/*
 * This file is part of Arkham Companion.
 *
 *  Arkham Companion is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Arkham Companion is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Arkham Companion.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.poquets.arkham.GUI;

import java.io.IOException;

import com.poquets.arkham.AHFlyweightFactory;
import com.poquets.arkham.Expansion;
import com.poquets.arkham.ExpansionCursor;
import com.poquets.arkham.GameState;
import com.poquets.arkham.R;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.CompoundButtonCompat;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class ExpansionSelector extends AppCompatActivity {
	private ListView lv1;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expansion);
        
        AHFlyweightFactory.INSTANCE.Init(this.getApplicationContext());
        
        //GameState.getInstance().onRestoreInstanceState(savedInstanceState);
        
        lv1=(ListView)findViewById(R.id.ListView01);
        Cursor cursor = new ExpansionCursor(AHFlyweightFactory.INSTANCE.getExpansions());
        	
        //Cursor cursor = getContentResolver().query(People.CONTENT_URI, new String[] {People._ID, People.NAME, People.NUMBER}, null, null, null);
        //startManagingCursor(cursor);
 
        // the desired columns to be bound
        String[] columns = new String[] { "Checked" };
        // the XML defined views which the data will be bound to
        int[] to = new int[] { R.id.number_entry };
 
        //final Bundle bundle = new Bundle();
        //final Random myRandom = new Random();
        // create the adapter using the cursor pointing to the desired data as well as the layout information
        //TODO replace the deprecated conctructor
        SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(this, R.layout.checkboxlist, cursor, columns, to, 0);
        //SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(/* ur stuff */);
        mAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            public boolean setViewValue(View view, final Cursor cursor, int columnIndex) {
            	if(columnIndex == 2) {
            		Typeface tf = Typeface.createFromAsset(getAssets(),"fonts/se-caslon-ant.ttf");
            		
            		CheckBox cb = (CheckBox) view;
            		//Checkbox view is reused when scrolling. Removed previous check listener or things will check and uncheck randomly.
            		cb.setOnCheckedChangeListener(null);
            		Expansion exp = ((ExpansionCursor)cursor).getExpansion();
            		cb.setChecked(exp.getApplied());
            		
            		StateListDrawable myStates = new StateListDrawable();
            	    int stateChecked = android.R.attr.state_checked;

            	    Bitmap checkOffBMP;
					try {
						BitmapFactory.Options opts = new BitmapFactory.Options();
					    opts.inScaled = true;
					    opts.inDensity = 120;//DisplayMetrics.DENSITY_MEDIUM;
					    Rect padding = new Rect();
					    opts.inTargetDensity = view.getResources().getDisplayMetrics().densityDpi;
						checkOffBMP = BitmapFactory.decodeStream(getAssets().open(((ExpansionCursor)cursor).getExpansion().getCheckboxOffPath()), padding, opts);
					
	            	                                BitmapDrawable checkOffDrawable = new BitmapDrawable(getResources(), checkOffBMP);
	            	    myStates.addState(new int[]{ -stateChecked }, checkOffDrawable);
	            	    Bitmap checkOnBMP = BitmapFactory.decodeStream(getAssets().open(((ExpansionCursor)cursor).getExpansion().getCheckboxOnPath()), padding, opts);
	            	                                BitmapDrawable checkOnDrawable = new BitmapDrawable(getResources(), checkOnBMP);
	            	    myStates.addState(new int[]{ stateChecked }, checkOnDrawable);
	            	    cb.setButtonDrawable(myStates);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
            		cb.setTypeface(tf);
            		cb.setTextSize(27);
            		
            		Expansion currentExp = ((ExpansionCursor)cursor).getExpansion();
            		
            		// Base game (ID 1) is required - show it as always checked
            		if (currentExp.getID() == 1) {
            			cb.setText(currentExp.getName() + " (Required)");
            			cb.setEnabled(false); // Disable the checkbox so it can't be unchecked
            		} else {
            			cb.setText(currentExp.getName());
            			cb.setEnabled(true);
            		}
            		
                    cb.setOnCheckedChangeListener(new OnCheckedChangeListener()
                    {
            			private Expansion exp = ((ExpansionCursor)cursor).getExpansion();
            			//private ArrayList<Encounter> encounters = loc.getEncounters();
						public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                        {
							// Prevent unchecking the base game
							if (exp.getID() == 1 && !isChecked) {
								buttonView.setChecked(true);
								android.widget.Toast.makeText(ExpansionSelector.this, 
									"Base game is required and cannot be deselected", 
									android.widget.Toast.LENGTH_SHORT).show();
								return;
							}
							
							GameState.getInstance().applyExpansion(exp.getID(), isChecked);
							//AHFlyweightFactory.INSTANCE.getCurrentLocations();
						}
            		
            		});
            		
            		return true;
            	}
        	            
                return false;
            }
        });

        	  
        // set this adapter as your ListActivity's adapter
        lv1.setAdapter(mAdapter);
        
        // Add question mark icon to logo
        addQuestionMarkIcon();
        
        // Show disclaimer dialog on first launch or if "Don't show again" was not selected
        showDisclaimerIfNeeded();
    }
    
    /**
     * Adds a question mark icon to the right side of the screen
     */
    private void addQuestionMarkIcon() {
        FrameLayout logoFrameLayout = findViewById(R.id.logoFrameLayout);
        
        if (logoFrameLayout != null) {
            int paddingPx = 5; // 5px padding as requested
            int iconSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, getResources().getDisplayMetrics());
            
            // Create ImageView for question mark icon
            ImageView questionMarkIcon = new ImageView(this);
            questionMarkIcon.setImageResource(android.R.drawable.ic_menu_help);
            questionMarkIcon.setContentDescription("Show Disclaimer");
            
            // Set 5px padding to increase clickable area
            questionMarkIcon.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);
            
            // Set layout parameters - include padding in the size for proper touch target
            int totalSize = iconSize + (paddingPx * 2);
            FrameLayout.LayoutParams questionParams = new FrameLayout.LayoutParams(
                totalSize,
                totalSize
            );
            questionMarkIcon.setLayoutParams(questionParams);
            
            // Set white tint for the icon
            questionMarkIcon.setColorFilter(0xFFFFFFFF);
            
            // Make it clickable and ensure it receives touch events
            questionMarkIcon.setClickable(true);
            questionMarkIcon.setFocusable(true);
            questionMarkIcon.setFocusableInTouchMode(true);
            questionMarkIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    android.util.Log.d("ExpansionSelector", "Question mark icon clicked - showing disclaimer");
                    try {
                        // Show disclaimer with checkbox when question mark is clicked
                        showDisclaimer(true);
                    } catch (Exception e) {
                        android.util.Log.e("ExpansionSelector", "Error showing disclaimer: " + e.getMessage(), e);
                    }
                }
            });
            
            // Also add a long click listener as backup
            questionMarkIcon.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    android.util.Log.d("ExpansionSelector", "Question mark icon long clicked - showing disclaimer");
                    try {
                        showDisclaimer(true);
                    } catch (Exception e) {
                        android.util.Log.e("ExpansionSelector", "Error showing disclaimer: " + e.getMessage(), e);
                    }
                    return true;
                }
            });
            
            logoFrameLayout.addView(questionMarkIcon);
            
            android.util.Log.d("ExpansionSelector", "Question mark icon added to logo with size: " + iconSize);
        } else {
            android.util.Log.e("ExpansionSelector", "logoFrameLayout is null!");
        }
    }
    
    private void showDisclaimerIfNeeded() {
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        boolean disclaimerShown = prefs.getBoolean("disclaimer_shown", false);
        
        // Show disclaimer if it hasn't been shown, OR if "Don't show again" was NOT selected
        if (!disclaimerShown) {
            showDisclaimer(true);
        }
    }
    
    /**
     * Shows the disclaimer dialog
     * @param showCheckbox Whether to show the "Don't show again" checkbox
     */
    private void showDisclaimer(boolean showCheckbox) {
            // Create a scrollable TextView for the message to handle tablets better
            ScrollView scrollView = new ScrollView(this);
            // Use Arkham background
            scrollView.setBackgroundResource(R.drawable.cthulhu_background);
            
            // Create a FrameLayout wrapper to add the border
            int borderWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
            FrameLayout borderWrapper = new FrameLayout(this);
            borderWrapper.setBackgroundColor(0xFFFFFFFF); // White border color
            
            // Add scrollView to borderWrapper with margin to create border effect
            FrameLayout.LayoutParams scrollParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            );
            scrollParams.setMargins(borderWidth, borderWidth, borderWidth, borderWidth);
            scrollView.setLayoutParams(scrollParams);
            borderWrapper.addView(scrollView);
            
            // Create a LinearLayout to hold both message and checkbox
            LinearLayout contentLayout = new LinearLayout(this);
            contentLayout.setOrientation(LinearLayout.VERTICAL);
            
            int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
            
            // Create title TextView - centered and large
            TextView titleView = new TextView(this);
            titleView.setText("⚠️ CRITICAL INFORMATION - READ FIRST");
            titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            titleView.setTextColor(0xFFFFFFFF); // White text to match app theme
            titleView.setGravity(android.view.Gravity.CENTER);
            titleView.setPadding(padding, padding, padding, padding / 2);
            titleView.setTypeface(null, android.graphics.Typeface.BOLD);
            LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            );
            titleView.setLayoutParams(titleParams);
            contentLayout.addView(titleView);
            
            TextView messageView = new TextView(this);
            messageView.setText(
                "⚠️ NOT AFFILIATED WITH FANTASY FLIGHT GAMES\n" +
                "This app is NOT affiliated with Fantasy Flight Games in any way.\n" +
                "Always refer to official Fantasy Flight Games materials.\n\n" +
                "⚠️ AGE LIMITATION - 14+\n" +
                "This app is recommended for ages 14+ as per the game publisher.\n" +
                "Content is based on H.P. Lovecraft Mythos (user discretion advised).\n\n" +
                "APP PURPOSE:\n" +
                "This app is designed to REPLACE physical cards to randomize cards and save table space.\n\n" +
                "⚠️ CRITICAL: In case of ANY doubt, the PHYSICAL CARDS are ALWAYS the truth.\n" +
                "The app may contain bugs, errors, or inaccuracies.\n\n" +
                "KEY INFORMATION:\n" +
                "• This app is FREE and in PERPETUAL BETA STATUS\n" +
                "• Cards are stored in a LOCAL DATABASE on your device only\n" +
                "• The database cannot be changed and requires NO user information\n" +
                "• The app does NOT save any user information\n" +
                "• The app does NOT offer any type of communication\n" +
                "• Works completely OFFLINE - no WiFi or network signal needed\n" +
                "• Language: English ONLY (regardless of your country/location)\n" +
                "• The app does NOT use your location\n\n" +
                "IMPORTANT:\n" +
                "⚠️ You must ALWAYS refer to original physical game materials if there is any doubt.\n" +
                "⚠️ Any issues with the app do NOT prevent you from using the real physical cards.\n" +
                "⚠️ The developer assumes NO LIABILITY for any issues, errors, or consequences.\n" +
                "⚠️ You use this app at your own risk.\n\n" +
                "By continuing, you acknowledge that you have read and agree to the full Privacy Policy & Terms of Service."
            );
            messageView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            messageView.setTextColor(0xFFFFFFFF); // White text to match app theme
            messageView.setPadding(padding, padding / 2, padding, padding);
            LinearLayout.LayoutParams messageParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            );
            messageView.setLayoutParams(messageParams);
            contentLayout.addView(messageView);
            
            // Create checkbox to save preference (only if showCheckbox is true)
            CheckBox dontShowAgainCheckbox = null;
            if (showCheckbox) {
                dontShowAgainCheckbox = new CheckBox(this);
                dontShowAgainCheckbox.setText("Don't show this again");
                dontShowAgainCheckbox.setTextColor(0xFFFFFFFF); // White text
                dontShowAgainCheckbox.setPadding(padding, padding / 2, padding, padding);
                setCheckboxWhite(dontShowAgainCheckbox);
                
                // Check if the preference was previously saved
                SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
                boolean disclaimerShown = prefs.getBoolean("disclaimer_shown", false);
                dontShowAgainCheckbox.setChecked(disclaimerShown);
            }
            
            if (dontShowAgainCheckbox != null) {
                LinearLayout.LayoutParams checkboxParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                );
                dontShowAgainCheckbox.setLayoutParams(checkboxParams);
                contentLayout.addView(dontShowAgainCheckbox);
            }
            scrollView.addView(contentLayout);
            
            // Set maximum height for tablets (60% of screen height)
            // Note: scrollView is a child of borderWrapper (FrameLayout), so we need to update its existing FrameLayout.LayoutParams
            int maxHeight = (int) (getResources().getDisplayMetrics().heightPixels * 0.6);
            scrollParams.height = maxHeight;
            scrollView.setLayoutParams(scrollParams);
            
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(borderWrapper);
            final CheckBox finalCheckbox = dontShowAgainCheckbox;
            builder.setPositiveButton("I Understand", (dialog, which) -> {
                SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
                // Only save preference if checkbox exists and is checked
                if (finalCheckbox != null && finalCheckbox.isChecked()) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("disclaimer_shown", true);
                    editor.apply();
                } else if (finalCheckbox != null && !finalCheckbox.isChecked()) {
                    // If checkbox is unchecked, clear the preference so disclaimer shows again next time
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("disclaimer_shown", false);
                    editor.apply();
                }
                dialog.dismiss();
            });
            builder.setCancelable(false);
            
            AlertDialog dialog = builder.create();
            dialog.show();
            
            // Set dialog background to match app theme (dark/transparent)
            if (dialog.getWindow() != null) {
                // Make dialog background transparent to show app background
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                
                // Ensure dialog doesn't take up entire screen on tablets
                int maxWidth = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
                dialog.getWindow().setLayout(maxWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
            }
    }
    
    private void setCheckboxWhite(CheckBox checkbox) {
        checkbox.setTextColor(android.graphics.Color.WHITE);
        CompoundButtonCompat.setButtonTintList(checkbox, android.content.res.ColorStateList.valueOf(android.graphics.Color.WHITE));
    }
    
//    @Override
//    public void onSaveInstanceState(Bundle savedInstanceState) {
//      super.onSaveInstanceState(savedInstanceState);
//      GameState.getInstance().onSaveInstanceState(savedInstanceState);
//      // Save UI state changes to the savedInstanceState.
//      // This bundle will be passed to onCreate if the process is
//      // killed and restarted.
////      savedInstanceState.putBoolean("MyBoolean", true);
////      savedInstanceState.putDouble("myDouble", 1.9);
////      savedInstanceState.putInt("MyInt", 1);
////      savedInstanceState.putString("MyString", "Welcome back to Android");
//      // etc.
//    }
    
//    @Override
//    public void onRestoreInstanceState(Bundle savedInstanceState) {
//      super.onRestoreInstanceState(savedInstanceState);
//      // Restore UI state from the savedInstanceState.
//      // This bundle has also been passed to onCreate.
//      GameState.getInstance().onRestoreInstanceState(savedInstanceState);
//    }
    
    public void openNeighborhood(View view)
    {
    	Intent i = new Intent(this, NeighborhoodSelector.class);
		this.startActivity(i);
    }
    
    public void openOW(View view)
    {
    	Intent i = new Intent(this, OtherworldSelector.class);
		this.startActivity(i);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.expansion_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
		int itemId = item.getItemId();
		if (itemId == R.id.new_game) {
			newGame();
			return true;
		} else if (itemId == R.id.about_app) {
			about();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
    }
    
    private void about()
    {
    	Intent i = new Intent(this, AboutActivity.class);
		this.startActivity(i);
    }
    

	private void newGame() {
		GameState.getInstance().newGame();
		lv1.setAdapter(lv1.getAdapter());
	}
}