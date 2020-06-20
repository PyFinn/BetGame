/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.betgame.app.recycler_view_adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.betgame.app.Game;
import com.betgame.app.R;

import java.util.ArrayList;


public class UpcomingGamesAdapter extends RecyclerView.Adapter<UpcomingGamesAdapter.ForecastAdapterViewHolder> {

    private ArrayList<Game> mQueriedGames = new ArrayList<Game>();
    private Game[] mGameArray;
    private boolean found1 = false;
    private boolean found2 = false;
    private boolean found3 = false;

    private final ForecastAdapterOnClickHandler mClickHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface ForecastAdapterOnClickHandler {
        void onClick(Game gameActual);
    }

    /**
     * Creates a ForecastAdapter.
     *
     * @param clickHandler The on-click handler for this adapter. This single handler is called
     *                     when an item is clicked.
     */
    public UpcomingGamesAdapter(ForecastAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public class ForecastAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        private final TextView mHomeTeamTexView;
        private final TextView mAwayTeamTextView;
        private final TextView mDateTextView;
        private final TextView mTimeTextView;
        private final TextView mLeagueTextView;
        private final TextView mOddHomeTeamTextView;
        private final TextView mOddAwayTeamTextView;
        private final TextView mOddDrawTextView;

        public ForecastAdapterViewHolder(View view) {
            super(view);
            mHomeTeamTexView = (TextView) view.findViewById(R.id.tv_home_team_name);
            mAwayTeamTextView = (TextView) view.findViewById(R.id.tv_away_team_name);
            mDateTextView = (TextView) view.findViewById(R.id.tv_date_match);
            mTimeTextView = (TextView) view.findViewById(R.id.tv_time_match);
            mLeagueTextView = (TextView) view.findViewById(R.id.tv_league_match);
            mOddHomeTeamTextView = (TextView) view.findViewById(R.id.tv_odd_home_team);
            mOddAwayTeamTextView = (TextView) view.findViewById(R.id.tv_odd_away_team);
            mOddDrawTextView = (TextView) view.findViewById(R.id.tv_odd_draw);
            view.setOnClickListener(this);
        }

        /**
         * This gets called by the child views during a click.
         *
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Game gameActual = mGameArray[adapterPosition];
            mClickHandler.onClick(gameActual);
        }
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (which ours doesn't) you
     *                  can use this viewType integer to provide a different layout. See
     *                  {@link android.support.v7.widget.RecyclerView.Adapter#getItemViewType(int)}
     *                  for more details.
     * @return A new ForecastAdapterViewHolder that holds the View for each list item
     */
    @Override
    public ForecastAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.game_display_rv_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new ForecastAdapterViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the weather
     * details for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param forecastAdapterViewHolder The ViewHolder which should be updated to represent the
     *                                  contents of the item at the given position in the data set.
     * @param position                  The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(ForecastAdapterViewHolder forecastAdapterViewHolder, int position) {
        Game thisGame = mGameArray[position];
        forecastAdapterViewHolder.mHomeTeamTexView.setText(thisGame.getHome_team());
        forecastAdapterViewHolder.mAwayTeamTextView.setText(thisGame.getAway_team());
        forecastAdapterViewHolder.mDateTextView.setText(thisGame.getDate());
        forecastAdapterViewHolder.mTimeTextView.setText(thisGame.getTime());
        forecastAdapterViewHolder.mLeagueTextView.setText(thisGame.getLeague());
        forecastAdapterViewHolder.mOddHomeTeamTextView.setText(thisGame.getOdd_home_team());
        forecastAdapterViewHolder.mOddAwayTeamTextView.setText(thisGame.getOdd_away_team());
        forecastAdapterViewHolder.mOddDrawTextView.setText(thisGame.getOdd_draw());
    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our forecast
     */
    @Override
    public int getItemCount() {
        if (null == mGameArray) return 0;
        return mGameArray.length;
    }

    /**
     * This method is used to set the weather forecast on a ForecastAdapter if we've already
     * created one. This is handy when we get new data from the web but don't want to create a
     * new ForecastAdapter to display it.
     *
     * The new weather data to be displayed.
     */
    public void setWeatherData(ArrayList<Game> games, long[] longList) {
        if (games == null){
            mGameArray = null;
        }else {
            for (Game game : games) {
                for (int i = 0; i < longList.length; i++){
                    if (game.getDateMS() == longList[i]) {
                        if (mQueriedGames.size() >= 3){
                            break;
                        }
                        mQueriedGames.add(game);
                        break;
                    }
                }
            }
            try {
                mGameArray = new Game[mQueriedGames.size()];
                mGameArray = mQueriedGames.toArray(mGameArray);
            } catch (NullPointerException e) {

            }
            notifyDataSetChanged();
        }
    }
}