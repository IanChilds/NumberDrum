package binnie.apps;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created with IntelliJ IDEA.
 * User: Binnie
 * Date: 02/04/13
 * Time: 21:21
 * To change this template use File | Settings | File Templates.
 */
public class Statistics extends Activity {
    TextView successesText;
    TextView failuresText;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics);

        successesText = (TextView)findViewById(R.id.successes);
        successesText.setText("Successes: " + Integer.toString(StatsStore.getSuccessesEver()));

        failuresText = (TextView)findViewById(R.id.failures);
        failuresText.setText("Failures: " + Integer.toString(StatsStore.getFailuresEver()));
    }
}