package binnie.apps;

import android.app.Activity;
import android.content.Intent;
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
    }
}
