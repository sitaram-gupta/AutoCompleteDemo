package com.chitooo.autocompletedemo;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ActivityTest extends Activity implements OnQueryTextListener {

    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_TERM = "term";
    private static final String DEFAULT = "default";

    private SearchManager searchManager;
    private SearchView searchView;
    private MenuItem searchMenuItem;
    private SuggestAdapter suggestionsAdapter;
    private final ArrayList<String> suggestionsArray = new ArrayList<String>();
    private final ArrayList<String> dummyArray = new ArrayList<String>();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create some dummy entries
        dummyArray.add("apples");
        dummyArray.add("oranges");
        dummyArray.add("bananas");
        dummyArray.add("pears");
        dummyArray.add("plums");

    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);

        searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.menu_search);

        searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(this);

        final MatrixCursor matrixCursor = getCursor(suggestionsArray);
        suggestionsAdapter = new SuggestAdapter(this, matrixCursor, suggestionsArray);
        searchView.setSuggestionsAdapter(suggestionsAdapter);
        suggestionsAdapter.notifyDataSetChanged();

        return true;
    }

    @Override
    public boolean onQueryTextChange(final String newText) {

        suggestionsArray.clear();

        for (int i = 0; i < dummyArray.size(); i++) {

            if (dummyArray.get(i).contains(newText)) {
                suggestionsArray.add(dummyArray.get(i));
            }
        }

        final MatrixCursor matrixCursor = getCursor(suggestionsArray);
        suggestionsAdapter = new SuggestAdapter(this, matrixCursor, suggestionsArray);
        searchView.setSuggestionsAdapter(suggestionsAdapter);
        suggestionsAdapter.notifyDataSetChanged();

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(final String query) {
        // TODO Auto-generated method stub
        return false;
    }

    private class SuggestAdapter extends CursorAdapter implements OnClickListener {

        private final ArrayList<String> mObjects;
        private final LayoutInflater mInflater;
        private TextView tvSearchTerm;

        public SuggestAdapter(final Context ctx, final Cursor cursor, final ArrayList<String> mObjects) {
            super(ctx, cursor, 0);

            this.mObjects = mObjects;
            this.mInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View newView(final Context ctx, final Cursor cursor, final ViewGroup parent) {
            final View view = mInflater.inflate(R.layout.item_list, parent, false);

            tvSearchTerm = (TextView) view.findViewById(R.id.tvSearchTerm);

            return view;
        }

        @Override
        public void bindView(final View view, final Context ctx, final Cursor cursor) {

            tvSearchTerm = (TextView) view.findViewById(R.id.tvSearchTerm);

            final int position = cursor.getPosition();

            if (cursorInBounds(position)) {

                final String term = mObjects.get(position);
                tvSearchTerm.setText(term);

                view.setTag(position);
                view.setOnClickListener(this);

            } else {
                // Something went wrong
            }
        }

        private boolean cursorInBounds(final int position) {
            return position < mObjects.size();
        }

        @Override
        public void onClick(final View view) {

            final int position = (Integer) view.getTag();

            if (cursorInBounds(position)) {

                final String selected = mObjects.get(position);

                Toast.makeText(getApplicationContext(), selected, Toast.LENGTH_SHORT).show();

                // Do something

            } else {
                // Something went wrong
            }
        }
    }

    private MatrixCursor getCursor(final ArrayList<String> suggestions) {

        final String[] columns = new String[]{COLUMN_ID, COLUMN_TERM};
        final Object[] object = new Object[]{0, DEFAULT};

        final MatrixCursor matrixCursor = new MatrixCursor(columns);

        for (int i = 0; i < suggestions.size(); i++) {

            object[0] = i;
            object[1] = suggestions.get(i);

            matrixCursor.addRow(object);
        }

        return matrixCursor;
    }
}