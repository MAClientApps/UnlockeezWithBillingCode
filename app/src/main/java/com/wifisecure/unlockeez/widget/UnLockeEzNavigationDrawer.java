package com.wifisecure.unlockeez.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.IntDef;
import androidx.cardview.widget.CardView;
import com.wifisecure.unlockeez.R;
import com.wifisecure.unlockeez.data.UnLockeEzMenuItem;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

public class UnLockeEzNavigationDrawer extends RelativeLayout{
    protected Context unLockeEzNavigationDrawerContext;
    protected LayoutInflater unLockeEzNavigationDrawerLayoutInflater;
    protected List<UnLockeEzMenuItem> menuItemList;
    protected RelativeLayout unLockeEzNavigationDrawerRootLayout, unLockeEzNavigationDrawerAppBarRelativeLayout;
    protected CardView unLockeEzNavigationDrawerContainerCardView;
    protected TextView unLockeEzNavigationDrawerAppVarTitleTextView;
    protected ImageView unLockeEzNavigationDrawerMenuImageView;
    protected ScrollView unLockeEzNavigationDrawerMenuScrollView;
    protected LinearLayout unLockeEzNavigationDrawerMenuLinearLayout, unLockeEzNavigationDrawerContainerLinearLayout;
    private int appbarColor = R.color.white;
    private int appbarTitleTextColor = R.color.pure_black;
    private int menuItemSemiTransparentColor = R.color.transparent_black_percent_60;
    private int navigationDrawerBackgroundColor = R.color.white;
    private int primaryMenuItemTextColor = R.color.white;
    private int secondaryMenuItemTextColor =R.color.pure_black ;
    private int menuIconTintColor = R.color.pure_black;
    private float primaryMenuItemTextSize = 20;
    private float secondaryMenuItemTextSize = 20;

    //Other stuff
    private boolean navOpen=false;
    private int currentPos=0;
    float centerX,centerY;


    @IntDef({STATE_OPEN, STATE_CLOSED, STATE_OPENING, STATE_CLOSING})
    @Retention(RetentionPolicy.SOURCE)
    private @interface State {}
    public static final int STATE_OPEN = 0;
    public static final int STATE_CLOSED = 1;
    public static final int STATE_OPENING = 2;
    public static final int STATE_CLOSING = 3;
    private OnHamMenuClickListener onHamMenuClickListener;
    private OnMenuItemClickListener onMenuItemClickListener;
    private DrawerListener drawerListener;

    public UnLockeEzNavigationDrawer(Context context) {
        super(context);
    }

    public UnLockeEzNavigationDrawer(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.SNavigationDrawer,
                0, 0);
        setAttributes(a);
        a.recycle();

    }
    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if(unLockeEzNavigationDrawerContainerLinearLayout == null){
            super.addView(child, index, params);
        } else {
            unLockeEzNavigationDrawerContainerLinearLayout.addView(child, index, params);
        }
    }
    public void init(Context context){
        unLockeEzNavigationDrawerContext = context;
        unLockeEzNavigationDrawerLayoutInflater = LayoutInflater.from(context);
        View rootView = unLockeEzNavigationDrawerLayoutInflater.inflate(R.layout.widget_navigation_drawer, this, true);
        unLockeEzNavigationDrawerRootLayout = rootView.findViewById(R.id.rootLayout);
        unLockeEzNavigationDrawerAppBarRelativeLayout = rootView.findViewById(R.id.appBarRL);
        unLockeEzNavigationDrawerContainerCardView = rootView.findViewById(R.id.containerCV);
        unLockeEzNavigationDrawerAppVarTitleTextView = rootView.findViewById(R.id.appBarTitleTV);
        unLockeEzNavigationDrawerMenuImageView = rootView.findViewById(R.id.menuIV);
        unLockeEzNavigationDrawerMenuScrollView = rootView.findViewById(R.id.menuSV);
        unLockeEzNavigationDrawerMenuLinearLayout = rootView.findViewById(R.id.menuLL);
        unLockeEzNavigationDrawerContainerLinearLayout = rootView.findViewById(R.id.containerLL);
        menuItemList = new ArrayList<>();
        unLockeEzNavigationDrawerMenuImageView.setOnClickListener(view -> {
            hamMenuClicked();
            if(navOpen){
                closeDrawer();
            }
            else {
                openDrawer();
            }
        });
    }
    @SuppressLint({"ResourceAsColor", "UseCompatLoadingForDrawables"})
    protected void initMenu(){
        for(int i=0;i<menuItemList.size();i++){
            @SuppressLint("InflateParams") View view = LayoutInflater.from(getContext()).inflate(R.layout.menu_row_item,null);
            TextView titleTV = view.findViewById(R.id.titleTV);
            TextView titleTV1 = view.findViewById(R.id.titleTV1);
            ImageView backgroundIV = view.findViewById(R.id.backgroundIV);
            CardView backgroundCV = view.findViewById(R.id.backgroundCV);
            View tintView = (View) view.findViewById(R.id.tintView);
            tintView.setBackgroundColor(menuItemSemiTransparentColor);
            titleTV.setTextColor(R.color.pure_black);
            titleTV1.setTextColor(primaryMenuItemTextColor);
            titleTV.setTextSize(secondaryMenuItemTextSize);
            titleTV1.setTextSize(primaryMenuItemTextSize);
            final RelativeLayout rootRL = view.findViewById(R.id.rootRL);
            backgroundCV.setTag("cv"+i);
            System.out.println("Testing "+backgroundCV.getTag());
            titleTV.setTag("tv"+i);
            if(i>=1){
                backgroundCV.setVisibility(View.GONE);
                backgroundCV.animate().translationX(rootRL.getX()-backgroundCV.getWidth()).setDuration(1).start();
                titleTV.setVisibility(View.VISIBLE);
            }
            rootRL.setTag(i);
            rootRL.setOnClickListener(view1 -> {
                if(currentPos!=Integer.parseInt(view1.getTag().toString())){
                    final CardView backCV1 = (CardView) unLockeEzNavigationDrawerMenuLinearLayout.findViewWithTag("cv"+currentPos);
                    final TextView title1 = (TextView) unLockeEzNavigationDrawerMenuLinearLayout.findViewWithTag("tv"+currentPos);
                    backCV1.animate().translationX(rootRL.getX()-backCV1.getWidth()).setDuration(300).start();
                    currentPos=Integer.parseInt(view1.getTag().toString());
                    menuItemClicked(currentPos);
                    unLockeEzNavigationDrawerAppVarTitleTextView.setText(menuItemList.get(currentPos).getTitle());
                    final CardView backCV = (CardView) unLockeEzNavigationDrawerMenuLinearLayout.findViewWithTag("cv"+currentPos);
                    final TextView title = (TextView) unLockeEzNavigationDrawerMenuLinearLayout.findViewWithTag("tv"+currentPos);
                    backCV.setVisibility(View.INVISIBLE);
                    backCV.animate().translationX(rootRL.getX()-backCV.getWidth()).setDuration(1).start();
                    backCV.animate().translationX(rootRL.getX()).setDuration(300).start();
                    backCV.setVisibility(View.VISIBLE);
                    title.setVisibility(View.GONE);
                    final Handler handler1 = new Handler();
                    handler1.postDelayed(() -> {
                        backCV1.setVisibility(View.GONE);
                        title1.setVisibility(View.VISIBLE);
                    },300);
                   closeDrawer();
                }
                else{
                    menuItemClicked(currentPos);
                    closeDrawer();
                }
            });
            backgroundIV.setImageDrawable(getContext().getDrawable(menuItemList.get(i).getImageId()));
            titleTV.setText(menuItemList.get(i).getTitle());
            titleTV1.setText(menuItemList.get(i).getTitle());
            unLockeEzNavigationDrawerMenuLinearLayout.addView(view);
        }
    }
    public interface OnHamMenuClickListener{
        void onHamMenuClicked();
    }
    public interface OnMenuItemClickListener{
        void onMenuItemClicked(int position);
    }
    public interface DrawerListener {
        void onDrawerOpening();
        void onDrawerClosing();
        void onDrawerOpened();
        void onDrawerClosed();
        void onDrawerStateChanged(@State int newState);
    }
    public void setOnMenuItemClickListener(OnMenuItemClickListener onMenuItemClickListener) {
        this.onMenuItemClickListener = onMenuItemClickListener;
    }
    public void setDrawerListener(DrawerListener drawerListener) {
        this.drawerListener = drawerListener;
    }
    protected void hamMenuClicked(){
        if(onHamMenuClickListener!=null){
            onHamMenuClickListener.onHamMenuClicked();
        }
    }
    protected void menuItemClicked(int position){
        if(onMenuItemClickListener!=null){
            onMenuItemClickListener.onMenuItemClicked(position);
        }
    }
    protected void drawerOpened(){
        if(drawerListener!=null){
            drawerListener.onDrawerOpened();
            drawerListener.onDrawerStateChanged(STATE_OPEN);
        }
    }
    protected void drawerClosed(){
        System.out.println("Drawer Closing");
        if(drawerListener!=null){
            drawerListener.onDrawerClosed();
            drawerListener.onDrawerStateChanged(STATE_CLOSED);
        }
    }
    protected void drawerOpening(){
        if(drawerListener!=null){
            drawerListener.onDrawerOpening();
            drawerListener.onDrawerStateChanged(STATE_OPENING);
        }
    }
    protected void drawerClosing(){
        if(drawerListener!=null){
            drawerListener.onDrawerClosing();
            drawerListener.onDrawerStateChanged(STATE_CLOSING);
        }
    }
    public void closeDrawer(){
        drawerClosing();
        navOpen=false;
        final int[] stateSet = {android.R.attr.state_checked * (navOpen ? 1 : -1)};
        unLockeEzNavigationDrawerMenuImageView.setImageState(stateSet,true);
        unLockeEzNavigationDrawerAppVarTitleTextView.animate().translationX(centerX).start();
        unLockeEzNavigationDrawerContainerCardView.animate().translationX(unLockeEzNavigationDrawerRootLayout.getX()).translationY(unLockeEzNavigationDrawerRootLayout.getY()).setDuration(500).start();
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            drawerClosed();
            unLockeEzNavigationDrawerContainerCardView.setCardElevation((float) 0);
            unLockeEzNavigationDrawerContainerCardView.setRadius((float)0);
        },500);
    }
    public void openDrawer(){
        drawerOpening();
        navOpen=true;
        final int[] stateSet = {android.R.attr.state_checked * (navOpen ? 1 : -1)};
        unLockeEzNavigationDrawerMenuImageView.setImageState(stateSet,true);
        unLockeEzNavigationDrawerContainerCardView.setCardElevation((float) 100.0);
        unLockeEzNavigationDrawerContainerCardView.setRadius((float)60.0);
        unLockeEzNavigationDrawerAppVarTitleTextView.animate().translationX(centerX+ unLockeEzNavigationDrawerMenuImageView.getWidth()+ unLockeEzNavigationDrawerMenuImageView.getWidth()/4+ unLockeEzNavigationDrawerAppVarTitleTextView.getWidth()/2- unLockeEzNavigationDrawerAppBarRelativeLayout.getWidth()/2).start();
        unLockeEzNavigationDrawerContainerCardView.animate().translationX(unLockeEzNavigationDrawerRootLayout.getX() +(unLockeEzNavigationDrawerRootLayout.getWidth() / 8)+ (unLockeEzNavigationDrawerRootLayout.getWidth() / 2) ).translationY(250).setDuration(500).start();
        final Handler handler = new Handler();
        handler.postDelayed(this::drawerOpened,250);
    }
    protected void setAttributes(TypedArray attrs){
        setAppbarColor(attrs.getColor(R.styleable.SNavigationDrawer_appbarColor,getResources().getColor(appbarColor)));
        setAppbarTitleTextColor(attrs.getColor(R.styleable.SNavigationDrawer_appbarTitleTextColor,getResources().getColor(appbarTitleTextColor)));
        setMenuIconTintColor(attrs.getColor(R.styleable.SNavigationDrawer_HamMenuIconTintColor,getResources().getColor(menuIconTintColor)));
        setMenuItemSemiTransparentColor(attrs.getColor(R.styleable.SNavigationDrawer_HamMenuItemSemiTransparentColor,getResources().getColor(menuItemSemiTransparentColor)));
        setNavigationDrawerBackgroundColor(attrs.getColor(R.styleable.SNavigationDrawer_navigationDrawerBackgroundColor,getResources().getColor(navigationDrawerBackgroundColor)));
        setPrimaryMenuItemTextColor(attrs.getColor(R.styleable.SNavigationDrawer_navigationDrawerBackgroundColor,getResources().getColor(primaryMenuItemTextColor)));
        setSecondaryMenuItemTextColor(attrs.getColor(R.styleable.SNavigationDrawer_secondaryMenuItemTextColor,getResources().getColor(secondaryMenuItemTextColor)));
        setAppbarTitleTextSize(attrs.getDimension(R.styleable.SNavigationDrawer_appbarTitleTextSize,20));
        setPrimaryMenuItemTextSize(attrs.getDimension(R.styleable.SNavigationDrawer_primaryMenuItemTextSize,20));
        setSecondaryMenuItemTextSize(attrs.getDimension(R.styleable.SNavigationDrawer_secondaryMenuItemTextSize,20));
    }
    public void setMenuItemList(List<UnLockeEzMenuItem> menuItemList) {
        this.menuItemList = menuItemList;
        initMenu();
    }
    public void setAppbarColor(int appbarColor) {
        this.appbarColor = appbarColor;
        unLockeEzNavigationDrawerAppBarRelativeLayout.setBackgroundColor(appbarColor);
    }
    public void setAppbarTitleTextColor(int appbarTitleTextColor) {
        this.appbarTitleTextColor = appbarTitleTextColor;
        unLockeEzNavigationDrawerAppVarTitleTextView.setTextColor(appbarTitleTextColor);
    }
    public void setAppbarTitleTextSize(float appbarTitleTextSize) {
        unLockeEzNavigationDrawerAppVarTitleTextView.setTextSize(appbarTitleTextSize);
    }
    public void setMenuIconTintColor(int menuIconTintColor) {
        this.menuIconTintColor = menuIconTintColor;
        unLockeEzNavigationDrawerMenuImageView.setColorFilter(menuIconTintColor);
    }
    public void setMenuItemSemiTransparentColor(int menuItemSemiTransparentColor) {
        this.menuItemSemiTransparentColor = menuItemSemiTransparentColor;
        invalidate();
    }
    public void setNavigationDrawerBackgroundColor(int navigationDrawerBackgroundColor) {
        unLockeEzNavigationDrawerRootLayout.setBackgroundColor(navigationDrawerBackgroundColor);
        this.navigationDrawerBackgroundColor = navigationDrawerBackgroundColor;
    }
    public void setPrimaryMenuItemTextColor(int primaryMenuItemTextColor) {
        this.primaryMenuItemTextColor = primaryMenuItemTextColor;
        invalidate();
    }
    public void setSecondaryMenuItemTextColor(int secondaryMenuItemTextColor) {
        this.secondaryMenuItemTextColor = secondaryMenuItemTextColor;
        invalidate();
    }
    public void setPrimaryMenuItemTextSize(float primaryMenuItemTextSize) {
        this.primaryMenuItemTextSize = primaryMenuItemTextSize;
        invalidate();
    }
    public void setSecondaryMenuItemTextSize(float secondaryMenuItemTextSize) {
        this.secondaryMenuItemTextSize = secondaryMenuItemTextSize;
        invalidate();
    }
}