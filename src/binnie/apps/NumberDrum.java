package binnie.apps;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Binnie
 * Date: 24/03/13
 * Time: 17:47
 * To change this template use File | Settings | File Templates.
 */
public class NumberDrum extends Activity {
    private int[] nums = new int[6];
    private int targetNum;
    private int differenceInBrackets = 0;
    public static final int OPERATOR = 1;
    public static final int NUMBER = 2;
    public static final int LEFT_BRACKET = 3;
    public static final int RIGHT_BRACKET = 4;
    private int lastChar = OPERATOR;
    private int numbersUsed = 0;
    private String[] solution;
    private Button[] numberButtons = new Button[6];
    private Button targetButton;
    private TextView equationTextView;
    private String[] equation = new String[30];
    private int equationIndex = 0;
    private Chronometer timer;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.numberdrum_game);

        Intent intent = getIntent();
        if (intent.getBooleanExtra(MainMenu.IS_NEW_GAME, true)) {
            solution = generateNewGame();
        }

        setUpGame();
    }

    public void setUpGame() {

        numberButtons[0] = (Button) findViewById(R.id.num1);
        numberButtons[1] = (Button) findViewById(R.id.num2);
        numberButtons[2] = (Button) findViewById(R.id.num3);
        numberButtons[3] = (Button) findViewById(R.id.num4);
        numberButtons[4] = (Button) findViewById(R.id.num5);
        numberButtons[5] = (Button) findViewById(R.id.num6);

        for (int ii = 0; ii < 6; ii++) {
            numberButtons[ii].setText(solution[ii * 2]);
            numberButtons[ii].setWidth(60);
            nums[ii] = Integer.parseInt(solution[ii * 2]);
        }
        targetNum = Integer.parseInt(solution[11]);
        targetButton = (Button) findViewById(R.id.target_num);
        targetButton.setText(Integer.toString(targetNum));
        targetButton.setWidth(90);
        targetButton.setClickable(false);

        equationTextView = (TextView) findViewById(R.id.equation);
        equationTextView.setText("");

        timer = (Chronometer) findViewById(R.id.timer);
        timer.start();
    }

    public void numberPressed(View view) {
        if ((lastChar == OPERATOR) || (lastChar == LEFT_BRACKET)) {
            TextView textView = (TextView)view;
            equation[equationIndex] = textView.getText().toString();
            equationTextView.setText((equationTextView.getText().toString() + equation[equationIndex]));
            equationIndex++;
            lastChar = NUMBER;
            numbersUsed++;

            Button button = (Button) findViewById(view.getId());
            button.setEnabled(false);

            if ((numbersUsed == 6) && (differenceInBrackets == 0)) setSubmitButton();
        }
    }

    public void operatorPressed (View view) {
        if ((lastChar == NUMBER) || (lastChar == RIGHT_BRACKET)) {
            TextView textView = (TextView)view;
            equation[equationIndex] = textView.getText().toString();
            equationTextView.setText((equationTextView.getText().toString() + equation[equationIndex]));
            equationIndex++;
            lastChar = OPERATOR;
        }
    }

    // Should maybe only allow a number of these equal to the total number of numbers remaining. But doesn't matter too
    // much.
    public void leftBracketPressed (View view) {
        if (((lastChar == OPERATOR) || (lastChar == LEFT_BRACKET)) &&
            (differenceInBrackets <= (6-(numbersUsed + 2)))) {
            differenceInBrackets++;
            equation[equationIndex] = "(";
            equationTextView.setText((equationTextView.getText().toString() + equation[equationIndex]));
            equationIndex++;
            lastChar = LEFT_BRACKET;
        }
    }

    public void rightBracketPressed (View view) {
        if (((lastChar == NUMBER) || (lastChar == RIGHT_BRACKET)) &&
            (differenceInBrackets > 0)) {
            differenceInBrackets--;
            equation[equationIndex] = ")";
            equationTextView.setText((equationTextView.getText().toString() + equation[equationIndex]));
            equationIndex++;
            lastChar = RIGHT_BRACKET;

            if ((numbersUsed == 6) && (differenceInBrackets == 0)) setSubmitButton();
        }
    }

    public void submitAnswer (View view) {
        // Calculate the answer, and then if it's correct, show a successful dialog / activity.
        // Otherwise just show a message dialog, and then go back to the same screen.
        if ((evalEquation(equation, equationIndex)) == targetNum) {
            targetButton.setText("Success");
        }
        else {
            targetButton.setText(Integer.toString(targetNum));
            resetGame(findViewById(R.id.reset));
        }
    }

    private void setSubmitButton () {
        targetButton.setClickable(true);
        targetButton.setText("Submit Answer");
    }

    public void startNewGame (View view) {
        // Should raise a dialog here.  The dialog should say that the previous game will be counted negatively
        // towards any stats that are being counted.
        // Don't need this function at the moment to get a prototype going.
        resetGame(findViewById(R.id.reset));
        solution = generateNewGame();
        timer.stop();
        timer.setBase(SystemClock.elapsedRealtime());
        setUpGame();
    }

    public void resetGame (View view) {
        equationTextView.setText("");
        for (int ii = 0; ii < equation.length; ii++) equation[ii] = "";
        equationIndex = 0;
        numbersUsed = 0;
        differenceInBrackets = 0;
        lastChar = OPERATOR;
        for (Button button: numberButtons) button.setEnabled(true);
        targetButton.setClickable(false);
        targetButton.setText(Integer.toString(targetNum));
    }

    public void giveHint (View view) {
        TextView textView = (TextView)view;
        textView.setText(Integer.toString(evalEquation(equation, equationIndex)));
    }

    private String[] generateNewGame() {
        String[] returnString = new String[17];
        Random random = new Random();
        int equationValue;
        do {
            for (int ii = 0; ii < 6; ii++) returnString[2*ii] = Integer.toString(random.nextInt(10)+1);
            //for (int ii = 4; ii < 6; ii++) returnString[2*ii] = Integer.toString((random.nextInt(4)+1)*25);
            for (int ii = 0; ii < 5; ii++) returnString[2*ii+1] = getOpFromInt(random.nextInt(4));
            equationValue = evalEquation(returnString, 11);
        } while ((equationValue > 1000) || (equationValue <= 0));
        returnString[11] = Integer.toString(equationValue);
        return returnString;
        //return (new String[]{"1", "+", "2", "+", "3", "+", "4", "+", "5", "+", "6", "21"});
    }

    private String getOpFromInt(int number) {
        if (number == 0) return "*";
    //    if (number == 1) return "/";
        if (number == 2) return "-";
        else return "+";
    }

    // If any intermediate expressions are not positive integers, then return -1
    // If any division is not soluble, then return -2
    // Else return the number given by the equationTextView
    // Should not be called on an equation with bad syntax, and should not be called on an empty string.
    private int evalEquation (String[] equationArray, int arrayLength) {
        int firstHalf;
        int secondHalf;
        int ii;
        int numRightMinusNumLeft = 0;
        int lastPlusOrMinus = arrayLength - 1;
        if (arrayLength == 1) return Integer.parseInt(equationArray[0]);

        while ((lastPlusOrMinus > 0) &&
                !((numRightMinusNumLeft == 0) && ((equationArray[lastPlusOrMinus].equals("+")) || (equationArray[lastPlusOrMinus].equals("-"))))) {
            if (equationArray[lastPlusOrMinus].equals("(")) numRightMinusNumLeft--;
            if (equationArray[lastPlusOrMinus].equals(")")) numRightMinusNumLeft++;
            lastPlusOrMinus--;
        }

        if (lastPlusOrMinus > 0) {
            firstHalf = evalEquation(Arrays.copyOfRange(equationArray, 0, lastPlusOrMinus), lastPlusOrMinus);
            String operator = equationArray[lastPlusOrMinus];
            secondHalf = evalEquation(Arrays.copyOfRange(equationArray, lastPlusOrMinus + 1, arrayLength), arrayLength - lastPlusOrMinus - 1);
            return evalEquationApplyOp(firstHalf, operator, secondHalf);
        }
        else if (equationArray[arrayLength - 1].equals(")")) {
            numRightMinusNumLeft = 1;
            ii = arrayLength - 1;
            while (numRightMinusNumLeft > 0) {
                ii--;
                if (equationArray[ii].equals("(")) numRightMinusNumLeft--;
                if (equationArray[ii].equals(")")) numRightMinusNumLeft++;
            }
            if (ii == 0) return evalEquation(Arrays.copyOfRange(equationArray, 1, arrayLength - 1), arrayLength - 2);
            else {
                firstHalf = evalEquation(Arrays.copyOfRange(equationArray, 0, ii-1), ii-1);
                String operator = equationArray[ii-1];
                secondHalf = evalEquation(Arrays.copyOfRange(equationArray, ii, arrayLength), arrayLength-ii);
                return evalEquationApplyOp(firstHalf, operator, secondHalf);
            }
        }
        else {
            firstHalf = evalEquation(Arrays.copyOfRange(equationArray, 0, arrayLength - 2), arrayLength - 2);
            String operator = equationArray[arrayLength - 2];
            secondHalf = Integer.parseInt(equationArray[arrayLength - 1]);
            return evalEquationApplyOp(firstHalf, operator, secondHalf);
        }
    }

    private int evalEquationApplyOp (int firstHalf, String operator, int secondHalf) {
        if ((firstHalf == -1) || (secondHalf == -1)) return -1;
        if ((firstHalf == -2) || (secondHalf == -2)) return -2;

        if (operator.equals("+")) return firstHalf + secondHalf;
        if (operator.equals("-")) {
            if ((firstHalf - secondHalf) <= 0) return -1;
            else return firstHalf - secondHalf;
        }
        if (operator.equals("*")) return firstHalf * secondHalf;
        if (operator.equals("/")) {
            if ((firstHalf % secondHalf) != 0) return -2;
            else return firstHalf / secondHalf;
        }
        assert (false);
        return 0;
    }
}