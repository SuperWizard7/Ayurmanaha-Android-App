package com.ayurmanaha.ayurvedaquiz.db;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import androidx.sqlite.db.SupportSQLiteDatabase;
import java.util.ArrayList;
import java.util.List;

@Database(entities = {Question.class, Score.class, SelectedAnswers.class}, version = 1)
public abstract class QRoomDatabase extends RoomDatabase {
    public abstract QuestionDao questionDao();
    public abstract ScoreDao scoreDao();
    public abstract SelectedAnswersDao selectedAnswersDao();

    private static volatile QRoomDatabase questionRoomInstance;

    static QRoomDatabase getDatabase(final Context context) {
        if (questionRoomInstance == null) {
            synchronized (QRoomDatabase.class) {
                if (questionRoomInstance == null) {
                    questionRoomInstance = Room.databaseBuilder(context.getApplicationContext(),
                            QRoomDatabase.class, "question_database")
                            .addCallback(dbCallback).build();
                }
            }
        }
        return questionRoomInstance;
    }

    private static RoomDatabase.Callback dbCallback = new RoomDatabase.Callback() {
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            ArrayList<Question> questionList = new ArrayList<>();
            for(String s : questionsString){
                String[] ss = s.split("\\|");
                questionList.add(new Question(ss[0], ss[1], ss[2], ss[3]));
            }
            new PopulateDbAsyncTask(questionRoomInstance,questionList).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private QuestionDao questionDao;
        private List<Question> questions;

        private PopulateDbAsyncTask(QRoomDatabase db,List<Question> qList) {
            questionDao = db.questionDao();
            questions = qList;
        }

        @Override
        protected Void doInBackground(Void... params) {
            questionDao.insert(questions);
            return null;
        }
    }

    private static String[] questionsString = {
            "General Appearance|Average|Pleasant |Excellent","Body Size / Frame|Tall, Thin, Poor Physique / Slim|Medium Height & Moderate Physique|Stout, Big & Good Physique","Body Weight|Low / Underweight|Average|Heavy / Overweight","Skin|Dry, Rough|Soft, Warm, Moles|Soft, Cold & Wet Skin ","Hair|Scanty, Brown, Coarse, Brittle & Dry|Moderate, Fine, Soft, Straight, Gray, Bald|Abundant, Oily, Thick, Curly, Wavy","Forehead|Small|Medium With Wrinkles|Large","Eyes|Small, Sunken, Dry Active Black|Sharp, Bright , Red, Sensitive To Light|Big, Beautiful, Blue, Calm, Loving White","Lips|Thin, Small, Dry, Cracked, Black / Brown Tinge|Medium, Soft, Red, Inflamed Yellow|Smooth, Oily, Thick, Large And Pale","Teeth & Gums|Stick out, Big, Roomy, Thin Gums|Medium, Soft And Tender Gums|Healthy, White And Strong Gums","Neck|Thin & Tall|Medium|Big & Folded","Arm|Thin, Small And Poorly Developed|Medium|Thick Large And Long","Hands|Small, Thin Dry Old Rough|Medium Warm Pink|Large Thick, Cold, Firm And Oily","Joints|Small, Thin And Steady, Cracking|Medium Soft Loose|Thick And Well Built","Belly|Thin, Flat And Sunken|Moderate|Big And Pot Bellied","Hips|Slender And Thin|Moderate|Heavy, Big","Feet|Small, Thin, Dry, Darkish, Fissures|Medium, Soft Pink|Large, Thick, Smooth, White","Nails|Small, Thin, Dry, Rough And Brittle|Medium, Soft, Sharp, Pink, Firm And Oily|Large, Thick, Smooth, Oily And Polished","Physical Activity|Quick, Fast And Erratic|Medium And Goal Seeking|Slow And Steady","Faith|Erratic, Changeable, Rebellious, Short Tempered|Steady, Aggression|Constant, Conservative, Loyal","Emotion|Fearful, Anxious, Uncertainty|Confused, Anger Hate, Jealousy, Irritable|Calm, Greedy, Attachment","Moles|Absent|Present|Absent","Taste|Sweet, Sour And Salt|Sweet, Bitter And Astringent|Bitter, Pungent And Astringent",
            "Appetite|Irregular, Eating Small Quantity Frequently|Good, Larger Quantity Compared To Others|Less Appetite, Moderate Intake","Thirst|Changeable|Surplus|Sparse","Food Habits|Changing With Time|Eating Frequently|Constant And Slow Eating","Bowels|Usually Constipated, Takes Long Time To Evacuate|Large Amount Of Stool, Tendency Of Diarrhoea|Normal Quantity, Evacuates Without Any Difficulty","Perspiration|Less|Excessive|Moderate","Complexion|Dull Brown Darkish|Red Flushed|White And Pale","Sleep|Less Disturbed|Moderate But Sound|Excellent","Speech|Rapid, Unclear|Sharp, Penetrating|Slow And Monotonous","Dreams|Imaginative, Flying, Moving ,Restless, Nightmares|Colourful, Passionate, Conflict (War, Violence)|Nature, Aquatics, Romantic, Sentimental","Activities |Unsteady, Fast Movement, Quick Gait,Likes To Wander|Moderate|Slow & Steady Activities, Slow Movements",
            "Energy Levels|Fluctuates|Moderate|Energetic","Control Over Mind|Weak / No Control|Moderate / Subdued On Pressure|Good Control","Intellect|Wavering, Not Good|Intelligent, Brilliant But Wavering|Good","Memory|Poor, Grasps And Forgets Easily|Sharp And Clear|Slowly Grasps But Will Never Forget","Concentration|Unsteady And Unable To Concentrate|Can Concentrate Fairly Well|Steady And Good Concentration","Grasping Power|Quick|Moderate|Takes Long Time","Contentment|No Contentment|Sometimes Contended|Contended","Quarreling Nature|Quarrels Frequently|Quarrels Soon|Less Quarrelsome","Gratitude|Ungrateful|Often Grateful|Grateful To A Great Extent","Fear|Easily Gets Frightened|Frightened But Brave|Brave","Respect For Teachers & Elders|No Respect|To A Small Extent|Maximum","Planning|Short Term|Medium Term|Long Term","Oratory Skills|Moderate|Good|Excellent","Immunity / Resistance To Disease|Poor|Medium|Good","Cannot Tolerate|Cold|Heat|Not Specific","Voice|Rough, High Pitch|Clear, High Pitch|Deep, Pleasant","Speed Of Gait / Walking Style|Fast|Average|Slow And Steady","Veins And Tendons|Prominent|Not Prominent|Well Covered","Forgiving Nature|Sometimes|Never Forgives|Always Forgives","Thoughts|Wavering, |Fairly Steady At Thoughts And Decisions|Steady Thoughts","Tolerance / Patience|Absent|Sometimes|Always","Helping Attitude|Absent|Helps To Those Who Asks For Help|Always Helping By Nature","Brave / Courage |Not Present|Brave|Sometimes","Mood|Changes Frequently|Changes But Not Fast, Gets Happy Very Soon|Steady Mind","Reading Habit|Absent|Sometimes|Very Much","Competitive Capacity|Do Not Like Competition|Excellent Competitors|Handles Competitive Stress Easily","Strength|Less|Moderate|Good","Artistic Inclination|Present|Moderate|Absent"
    };
}
