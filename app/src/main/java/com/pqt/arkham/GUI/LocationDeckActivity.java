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
package com.pqt.arkham.GUI;

import java.io.IOException;
import java.util.ArrayList;

import com.pqt.arkham.AHFlyweightFactory;
import com.pqt.arkham.Encounter;
import com.pqt.arkham.GameState;
import com.pqt.arkham.NeighborhoodCard;
import com.pqt.arkham.R;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class LocationDeckActivity extends AppCompatActivity {
	//private Encounter encounter;
	
	private long neiID;
    /** Called when the activity is first created. */
    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locationdeck);
        
        AHFlyweightFactory.INSTANCE.Init(this.getApplicationContext());
        
        Bundle extras = getIntent().getExtras();

        neiID = extras.getLong("neighborhood");
        
        ViewPager viewpager = (ViewPager) findViewById(R.id.viewpager);
        viewpager.setAdapter(new CardAdapter(this, GameState.getInstance().getDeckByNeighborhood(neiID)));

        //viewpager.setBackgroundColor(Color.CYAN);
    }

	/*
    @Override
    public void onBackPressed() {
    	Toast.makeText(LocationDeckActivity.this, R.string.location_deck_back, Toast.LENGTH_SHORT).show();
    	super.onBackPressed();
    }
    */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d("LocationDeckActivity", "onCreateOptionsMenu called");
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.deck_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d("LocationDeckActivity", "onOptionsItemSelected called with item: " + item.getTitle());
		// Handle item selection
		int itemId = item.getItemId();
		if (itemId == R.id.shuffle) {
			Log.d("LocationDeckActivity", "Shuffle item selected");
			shuffleDeck();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}
    
    /*
	public void shuffleDeck()
    {
    	GameState.getInstance().randomizeNeighborhood(neiID);
    	ViewPager viewpager = (ViewPager) findViewById(R.id.viewpager);
        viewpager.setAdapter(new CardAdapter(this, GameState.getInstance().getDeckByNeighborhood(neiID)));
    }
    */

	public void shuffleDeck() {
		GameState.getInstance().randomizeNeighborhood(neiID);
		ViewPager viewpager = (ViewPager) findViewById(R.id.viewpager);
		// It's better to update the existing adapter rather than creating a new one
		// if the adapter instance is accessible here.
		// For this example, I'm assuming you need to create a new one as in your original code.
		CardAdapter newAdapter = new CardAdapter(this, GameState.getInstance().getDeckByNeighborhood(neiID));
		viewpager.setAdapter(newAdapter);
		// If you can reuse the existing adapter, you would update its data and then call:
		newAdapter.notifyDataSetChanged();
	}


    public class CardAdapter extends PagerAdapter {
	    //int mGalleryItemBackground;
	    private Context mContext;

	    private ArrayList<NeighborhoodCard> cardArr;
	    
	    private LayoutInflater mInflater;

	    public CardAdapter(Context c, ArrayList<NeighborhoodCard> cardArr) 
	    {
	        mContext = c;
	        this.cardArr = cardArr;
	        
	        Log.w("AHDecks", cardArr.size() + " cards in deck.");
	        
	        mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    }

	    public int getCount() {
	        return cardArr.size();
	    }
	    
	    @Override
	    public Object instantiateItem( ViewGroup container, int position )
	    {
	    	final NeighborhoodCard theCard = cardArr.get(position);
	    	final ArrayList<Encounter> encounters = theCard.getEncounters();

	    	final ScrollView layout = (ScrollView)mInflater.inflate(R.layout.cardlistitem, null);
            final LinearLayout cardContents = (LinearLayout) layout.findViewById(R.id.card_contents);
	    	//Set stuff

	    	TextView text = null;
	    	for(int i = 0; i < encounters.size(); i++)
	    	{
	    		final int idx = i;
		    	RelativeLayout header = (RelativeLayout)mInflater.inflate(R.layout.encounterheader, null);
		    	TextView title = (TextView)header.findViewById(R.id.titleTV1);
		    	title.setPadding(getIndependentWidth(title.getPaddingLeft()), getIndependentHeight(title.getPaddingTop()), getIndependentWidth(title.getPaddingRight()), getIndependentHeight(title.getPaddingBottom()));
		    	title.setText(encounters.get(i).getLocation().getLocationName());
		    	Typeface tf = Typeface.createFromAsset(getAssets(),
		                "fonts/se-caslon-ant.ttf");
		        title.setTypeface(tf);
		    	Button chooseEncounterBtn = ((Button)header.findViewById(R.id.button1));
		    	OnClickListener listener = new OnClickListener()
                {                	 
                	private Encounter enc = encounters.get(idx);

					public void onClick(View v) {
						GameState.getInstance().AddHistory(enc);
						GameState.getInstance().randomizeNeighborhood(theCard.getNeighborhood().getID());
						//shuffleDeck();

						Toast.makeText(LocationDeckActivity.this, R.string.encounter_arrow_clicked, Toast.LENGTH_SHORT).show();
						finish();
					}
                };
                

		    	header.setOnClickListener(listener);
		    	chooseEncounterBtn.setOnClickListener(listener);
		    	
		    	//header.setBackgroundColor(Color.CYAN);
		    	
//		    	// post a runnable to the parent view's message queue so its run after 
//	    	    // the view is drawn 
//	    	    pager.post(new Runnable() { 
//		    	    //  @Override 
//		    	      public void run() { 
//		    	        Rect delegateArea = new Rect(); 
//		    	        Button delegate = chooseEncounterBtn; 
//		    	        delegate.getHitRect(delegateArea); 
//		    	        delegateArea.top -= 50; 
//		    	        delegateArea.left -= 50;
//		    	        delegateArea.right += 50;
//		    	        delegateArea.bottom += 50;
//		    	        TouchDelegate expandedArea = new TouchDelegate(delegateArea, delegate); 
//		    	        // give the delegate to an ancestor of the view we're delegating the 
//		    	        // area to 
//		    	        if (View.class.isInstance(delegate.getParent())) { 
//		    	          layout.setTouchDelegate(expandedArea); 
//		    	        } 
//		    	      } 
//		    	    }); 
		    	
		    	
		    	text = (TextView)mInflater.inflate(R.layout.encountertext, null);
		    	                        text.setText(Html.fromHtml(encounters.get(i).getEncounterText(), Html.FROM_HTML_MODE_LEGACY));
		    	text.setPadding(getIndependentWidth(text.getPaddingLeft()), getIndependentHeight(text.getPaddingTop()), getIndependentWidth(text.getPaddingRight()), getIndependentHeight(text.getPaddingBottom()));
		    	
		    
		    	cardContents.addView(header);
		    	cardContents.addView(text);
	    	}
	    	
	    	//Last text fills the rest of the space
	    	if(text != null)
	    	{
	    		text.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f));
	    	}
	    	
	    	
	    	((ViewPager) container).addView(layout);
	    	
	        Bitmap front;
	        try {
	        	front = BitmapFactory.decodeStream(getAssets().open(theCard.getCardPath()));
			} catch (IOException e) {
				front = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.encounter_front);
			}
	        
	        //Bitmap expansion = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.blackgoaticon);
	        Bitmap result = overlayCard(front, theCard);
//	        byte[] chunk = front.getNinePatchChunk(); 
//	        NinePatchDrawable ninePatch = new NinePatchDrawable(getResources(), front, chunk, new Rect(), null);
//	        layout.setBackgroundDrawable(ninePatch);
	                        layout.setBackground(new BitmapDrawable(getResources(), result));
	    	
	    	return layout;
	    }
	    
	    @Override
	    public void destroyItem( ViewGroup container, int position, Object object )
	    {
	        ((ViewPager)container).removeView( (View)object );
	    }
	 
	    @Override
	    public boolean isViewFromObject( View view, Object object )
	    {
	        return view.equals( object );
	    }
	    
	    private Bitmap overlayCard(Bitmap bmp1, NeighborhoodCard card)
	    {
	    	Bitmap retBmp = bmp1;
	    	int totalWidth = 0;
	    
	    	String path = AHFlyweightFactory.INSTANCE.getExpansion(card.getExpIDs().get(0)).getExpansionIconPath();
	    	Bitmap expBmp = null;
	    	
	    	for(int i = 0; i < card.getExpIDs().size(); i++)
	    	{
	    		path = AHFlyweightFactory.INSTANCE.getExpansion(card.getExpIDs().get(i)).getExpansionIconPath();
	    	
		    	if(path != null)
		    	{
			    	try {
						expBmp = BitmapFactory.decodeStream(getAssets().open(path));
				        
					} catch (IOException e) {
						expBmp = null;
					}
		    	}
		    	
		    	retBmp = overlay(retBmp, expBmp, totalWidth+10);
		    	if(expBmp != null)
		    	{
		    		totalWidth += expBmp.getWidth();
		    	}
	    	}
	    	
	    	return retBmp;
	    }
	    
	    private Bitmap overlay(Bitmap bmp1, Bitmap bmp2, int rightMargin) 
	    {
			if(bmp2 ==null)
			{
				return bmp1;
			}
	    	//DisplayMetrics dm = new DisplayMetrics();
	        //getWindowManager().getDefaultDisplay().getMetrics(dm);
	        

	        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
	        Canvas canvas = new Canvas(bmOverlay);
	        canvas.drawBitmap(bmp1, 0,0, null);
	        Matrix mtx = new Matrix();
	        //float resizeHeightPercentage = bmp1.getHeight()/491.0f;
	        float resizeWidthPercentage = bmp1.getWidth()/305.0f;
	        float top = bmp1.getHeight() - (bmp2.getHeight()+10)*resizeWidthPercentage;
	        float left = bmp1.getWidth() - (bmp2.getWidth()+rightMargin)*resizeWidthPercentage;
	        mtx.setScale(resizeWidthPercentage, resizeWidthPercentage);
	        mtx.postTranslate(left, top);
	        Paint paint = new Paint();
	        paint.setFilterBitmap(true);
	        canvas.drawBitmap(bmp2, mtx, paint);
	        return bmOverlay;
	    }
	    
		protected int getIndependentWidth(int origWidth)
		{
			DisplayMetrics dm = getResources().getDisplayMetrics();
			return (int) Math.ceil((origWidth*dm.widthPixels)/480.0f);
		}
		
		protected int getIndependentHeight(int origHeight)
		{
			DisplayMetrics dm = getResources().getDisplayMetrics();
			return (int) Math.ceil((origHeight*dm.heightPixels)/800.0f);
		}
	}
}
