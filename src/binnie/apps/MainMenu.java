package binnie.apps;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainMenu extends Activity implements AdapterView.OnItemClickListener {
    public final static String IS_NEW_GAME = "binnie.apps.is_new_game";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setStats();

        ArrayList<String> optionList = new ArrayList<String>();
        optionList.add("New Game");
        optionList.add("Restore Previous Game");
        optionList.add("Instructions");
        optionList.add("Statistics");
        optionList.add("About");
        optionList.add("NumberDrum Solver");

        ListView gamesListView = (ListView)findViewById(R.id.main_menu_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainMenu.this, android.R.layout.simple_list_item_1, optionList);
        gamesListView.setAdapter(adapter);
        gamesListView.setOnItemClickListener(this);
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (i == 0) {
            Intent newGame = new Intent(this, NumberDrum.class);
            newGame.putExtra(IS_NEW_GAME, true);
            startActivity(newGame);
        }
        if (i == 3) {
            Intent statistics = new Intent(this, Statistics.class);
            startActivity(statistics);
        }
    }

    private void setStats () {
        SharedPreferences savedStats = getPreferences(MODE_PRIVATE);
        StatsStore.setSuccessesEver(savedStats.getInt("allSuccessesEver", 0));
        StatsStore.setFailuresEver(savedStats.getInt("failuresEver", 0));
    }

    private void saveStats() {
        SharedPreferences saveStats = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = saveStats.edit();
        editor.putInt("allSuccessesEver", StatsStore.getSuccessesEver());
        editor.putInt("failuresEver", StatsStore.getFailuresEver());
        editor.commit();
    }

    public void onStop() {
        super.onStop();
        saveStats();
    }

}
