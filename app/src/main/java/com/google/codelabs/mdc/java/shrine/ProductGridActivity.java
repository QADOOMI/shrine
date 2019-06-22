package com.google.codelabs.mdc.java.shrine;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.SearchManager;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;

import com.google.codelabs.mdc.java.shrine.network.ProductEntry;

public class ProductGridActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ProductCardAdapter adapter;
    private final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            adapter.getFilter().filter(query);
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            adapter.getFilter().filter(newText);
            return false;
        }
    };
    private final Animation.AnimationListener animationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            toolbar.setBackgroundColor(getThemeColor(getApplicationContext(), R.color.colorPrimaryDark));
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shr_product_grid_activity);

        initViews();
    }

    private void initViews() {
        setUpToolBar();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            findViewById(R.id.product_grid).setBackground(
                    this.getResources().getDrawable(R.drawable.shr_product_grid_background_shape, null));
        }

        final RecyclerView recyclerView = findViewById(R.id.recycler_view);
        final GridLayoutManager gridManager
                = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);

        gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position % 3 == 0 ? 2 : 1;
            }
        });

        adapter = new ProductCardAdapter(ProductEntry.initProductEntryList(getResources()));

        recyclerView.setLayoutManager(gridManager);
        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(adapter);
        int padding = getResources().getDimensionPixelSize(R.dimen.shr_product_grid_spacing);

        recyclerView.addItemDecoration(new ProductGridItemDecoration(padding, padding));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.shr_toolbar_menu, menu);

        MenuItemCompat.setOnActionExpandListener(menu.findItem(R.id.search), new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                if (item.isActionViewExpanded()) {
                    animateSearchToolbar(1, false, false);
                }
                return true;

            }


            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Called when SearchView is expanding
                animateSearchToolbar(1, true, true);
                return true;

            }
        });
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.search) {

            // Get the SearchView and set the searchable configuration
            SearchManager searchManager = (SearchManager) this.getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView = (SearchView) item.getActionView();

            EditText searchText = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);

            runOnUiThread(() -> {
                searchText.setTextColor(getResources().getColor(R.color.textColorPrimary));
                searchText.setHintTextColor(getResources().getColor(R.color.textColorPrimary));
            });

            // Assumes current activity is the searchable activity
            searchView.setSearchableInfo(searchManager.getSearchableInfo(this.getComponentName()));
            searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
            searchView.setQueryRefinementEnabled(true);
            searchView.setOnQueryTextListener(queryTextListener);


            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isLolliPopOrHigher() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    private int toolBarWidthBasedOnRTL(boolean containsOverflow, int numberOfMenuIcon) {
        return toolbar.getWidth() - (containsOverflow ?
                getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material) : 0)
                -
                ((getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material) * numberOfMenuIcon) / 2);
    }

    private void animateSearchToolbar(int numberOfMenuIcon, boolean containsOverflow, boolean show) {
        this.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.quantum_grey_600));

        if (show) {
            if (isLolliPopOrHigher()) {

                int width = toolBarWidthBasedOnRTL(containsOverflow, numberOfMenuIcon);

                Animator createCircularReveal = ViewAnimationUtils.createCircularReveal(toolbar,
                        isRtl(getResources()) ? toolbar.getWidth() - width : width
                        , toolbar.getHeight() / 2
                        , 0.0f, (float) width);

                createCircularReveal.setDuration(250);
                createCircularReveal.start();

            } else {

                final TranslateAnimation translateAnimation = new TranslateAnimation(0.0f
                        , 0.0f
                        , (float) (-toolbar.getHeight())
                        , 0.0f);

                translateAnimation.setDuration(220);

                toolbar.clearAnimation();

                this.runOnUiThread(() ->
                        toolbar.startAnimation(translateAnimation));
            }
        } else {
            if (isLolliPopOrHigher()) {
                int width = toolBarWidthBasedOnRTL(containsOverflow, numberOfMenuIcon);

                Animator createCircularReveal = ViewAnimationUtils.createCircularReveal(toolbar,
                        isRtl(getResources()) ? toolbar.getWidth() - width : width
                        , toolbar.getHeight() / 2
                        , (float) width, 0.0f);

                createCircularReveal.setDuration(500);
                createCircularReveal.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        ProductGridActivity.this.getWindow()
                                .setStatusBarColor(getThemeColor(ProductGridActivity.this, R.color.colorPrimaryDark));
                    }
                });
                createCircularReveal.start();
            } else {
                AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
                Animation translateAnimation = new TranslateAnimation(0.0f
                        , 0.0f
                        , 0.0f
                        , (float) (-toolbar.getHeight()));

                AnimationSet animationSet = new AnimationSet(true);
                animationSet.addAnimation(alphaAnimation);
                animationSet.addAnimation(translateAnimation);
                animationSet.setDuration(220);
                animationSet.setAnimationListener(animationListener);
                toolbar.startAnimation(animationSet);
            }
            this.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
    }

    private boolean isRtl(Resources resources) {
        return resources.getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }

    private static int getThemeColor(Context context, int id) {
        Resources.Theme theme = context.getTheme();
        TypedArray a = theme.obtainStyledAttributes(new int[]{id});
        int result = a.getColor(0, 0);
        a.recycle();
        return result;
    }

    private void setUpToolBar() {
        toolbar = findViewById(R.id.app_toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new NavigationIconClickListener(this
                , findViewById(R.id.product_grid)
                , new AccelerateDecelerateInterpolator()
                , this.getResources().getDrawable(R.drawable.shr_branded_menu)
                , this.getResources().getDrawable(R.drawable.shr_close_menu)
        ));
    }
}
