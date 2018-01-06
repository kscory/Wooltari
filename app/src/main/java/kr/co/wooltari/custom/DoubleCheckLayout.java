package kr.co.wooltari.custom;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Field;

import kr.co.wooltari.R;

/**
 * Created by Kyung on 2017-12-23.
 */

public class DoubleCheckLayout extends ConstraintLayout {

    private Button btnDoubleCheck;
    private EditText editText;
    private ConstraintSet constraintSet;

    private GradientDrawable btnGD;
    AnimatorSet beforeAniSet;
    AnimatorSet afterAniSet;

    private int layoutWidth;
    private int layoutHeight;
    private int btnWidth;
    private int btnHeight;

    private boolean isSuccess =false;
    private boolean isAnimated = false;
    private boolean isEnd= false;
    private boolean isCreate = false;

    public DoubleCheckLayout(Context context) {
        super(context);
        init();
    }

    public DoubleCheckLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DoubleCheckLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if(!isCreate) {
            layoutWidth = getWidth();
            layoutHeight = getHeight();
            initConstraint();
            initDoubleCheckButton();
            initEditText();
        }
        super.onWindowFocusChanged(hasWindowFocus);
    }

    @Override
    public void setEnabled(boolean enabled) {
        editText.setEnabled(enabled);
        btnDoubleCheck.setEnabled(enabled);
        super.setEnabled(enabled);
    }

    @SuppressLint("ResourceType")
    private void init(){
        constraintSet = new ConstraintSet();
        constraintSet.clone(this);
        isCreate = false;

        btnDoubleCheck = new Button(getContext());
        // setId를 안할 경우 default로 -1 값이 저장되므로 여러 위젯 사용시 setId를 넣어준다.
        btnDoubleCheck.setId(100);
        addView(btnDoubleCheck);

        editText = new EditText(getContext());
        editText.setId(101);
        addView(editText);
    }

    private void initConstraint(){
        // constraint layout 설정
        constraintSet.connect(btnDoubleCheck.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT);
        constraintSet.connect(btnDoubleCheck.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        constraintSet.connect(btnDoubleCheck.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        constraintSet.constrainHeight(btnDoubleCheck.getId(),layoutHeight);
        constraintSet.constrainWidth(btnDoubleCheck.getId(),layoutWidth/4);
        // constraint 적용
        constraintSet.applyTo(this);

        constraintSet.connect(editText.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT);
        constraintSet.connect(editText.getId(), ConstraintSet.RIGHT, btnDoubleCheck.getId(), ConstraintSet.LEFT);
        constraintSet.connect(editText.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        constraintSet.connect(editText.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        constraintSet.constrainHeight(editText.getId(),ConstraintSet.MATCH_CONSTRAINT);
        constraintSet.constrainWidth(editText.getId(),ConstraintSet.MATCH_CONSTRAINT);
        // constraint 적용
        constraintSet.applyTo(this);
    }

    private void initDoubleCheckButton(){
        btnDoubleCheck.setText(getResources().getString(R.string.user_sign_up_double_check));
        btnDoubleCheck.setTextColor(ContextCompat.getColor(getContext(),R.color.white));

        LayoutParams btnLayoutParams = (LayoutParams) btnDoubleCheck.getLayoutParams();
        btnWidth = btnLayoutParams.width;
        btnHeight = btnLayoutParams.height;

        setDoubleCheckBackground();
        setBtnCheckingAnimation();
        setDoubleCheckingClick();
        btnDoubleCheck.setTextSize(10);
        isCreate = true;
    }

    private void setDoubleCheckBackground(){
        btnDoubleCheck.setBackgroundResource(R.drawable.edittext_double_check);
        btnGD = new GradientDrawable();
        btnGD.setColor(Color.GRAY);
        btnGD.setCornerRadius(10);
        btnGD.setStroke(2,Color.DKGRAY);
        btnDoubleCheck.setBackground(btnGD);
    }

    private void setDoubleCheckingClick(){
        btnDoubleCheck.setOnClickListener(v -> {
            btnDoubleCheck.setText("");
            beforeAniSet.start();
            btnDoubleCheck.setEnabled(false);
        });
    }

    public void setDoubleCheckingClick(ButtonClickListener buttonClickListener){
        btnDoubleCheck.setOnClickListener(v -> {
            btnDoubleCheck.setText("");
            beforeAniSet.start();
            btnDoubleCheck.setEnabled(false);
            buttonClickListener.clickEffect();
        });
    }

    private void setBtnCheckingAnimation(){
        btnDoubleCheck.setPivotX(btnWidth/2);
        ObjectAnimator aniScale = ObjectAnimator.ofFloat(btnDoubleCheck, "scaleX", (float)btnHeight/(float)btnWidth);
        ValueAnimator aniColor = ValueAnimator.ofObject(new ArgbEvaluator(), Color.GRAY, ContextCompat.getColor(getContext(),R.color.wooltariColor));
        aniColor.addUpdateListener(animation -> btnGD.setColor((Integer)animation.getAnimatedValue()));
        ValueAnimator aniCircle = ValueAnimator.ofFloat(10, btnWidth);
        aniCircle.addUpdateListener(animation -> {
            btnGD.setCornerRadius((float)animation.getAnimatedValue());
            if((float)animation.getAnimatedValue()==btnWidth) btnGD.setShape(GradientDrawable.OVAL);
            else btnGD.setShape(GradientDrawable.RECTANGLE);
        });
        beforeAniSet = new AnimatorSet();
        beforeAniSet.playTogether(aniScale, aniColor, aniCircle);
        beforeAniSet.setDuration(500);
        beforeAniSet.setInterpolator(new AccelerateDecelerateInterpolator());

        beforeAniSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isAnimated = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimated = false;
                checkAfter();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });


        ObjectAnimator aniScaleRe = ObjectAnimator.ofFloat(btnDoubleCheck, "scaleX", (float)btnHeight/(float)btnWidth,1);
        ValueAnimator aniColorRe = ValueAnimator.ofObject(new ArgbEvaluator(), ContextCompat.getColor(getContext(),R.color.wooltariColor),Color.GRAY);
        aniColorRe.addUpdateListener(animation -> btnGD.setColor((Integer)animation.getAnimatedValue()));
        ValueAnimator aniCircleRe = ValueAnimator.ofFloat(btnWidth, 10);
        aniCircleRe.addUpdateListener(animation -> btnGD.setCornerRadius((float)animation.getAnimatedValue()));
        afterAniSet = new AnimatorSet();
        afterAniSet.playTogether(aniScaleRe, aniColorRe, aniCircleRe);
        afterAniSet.setDuration(200);
        afterAniSet.setInterpolator(new AccelerateDecelerateInterpolator());
    }

    public void setSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
        isEnd = true;
        checkAfter();
    }

    private void checkAfter(){
        if(!isAnimated && isEnd) {
            if (isSuccess) {
                btnDoubleCheck.setText("\u221A");
                btnDoubleCheck.setTextSize(20);
            } else {
                btnDoubleCheck.setEnabled(true);
                btnGD.setShape(GradientDrawable.RECTANGLE);
                afterAniSet.start();
                btnDoubleCheck.setText(getResources().getString(R.string.user_sign_up_double_check));
            }
            isEnd = false;
        }
    }

    private void initEditText(){
        editText.setSingleLine();
        editText.setInputType( InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS |InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        editText.setSingleLine();
        // 커서색상을 변경
        try {
            Field fCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            fCursorDrawableRes.setAccessible(true);
            int mCursorDrawableRes = fCursorDrawableRes.getInt(editText);
            Field fEditor = TextView.class.getDeclaredField("mEditor");
            fEditor.setAccessible(true);
            Object editor = fEditor.get(editText);
            Class<?> clazz = editor.getClass();
            Field fCursorDrawable = clazz.getDeclaredField("mCursorDrawable");
            fCursorDrawable.setAccessible(true);

            Drawable[] drawables = new Drawable[2];
            Resources res = editText.getContext().getResources();
            drawables[0] = res.getDrawable(mCursorDrawableRes);
            drawables[1] = res.getDrawable(mCursorDrawableRes);
            drawables[0].setColorFilter(ContextCompat.getColor(getContext(),R.color.wooltariColor), PorterDuff.Mode.SRC_IN);
            drawables[1].setColorFilter(ContextCompat.getColor(getContext(),R.color.wooltariColor), PorterDuff.Mode.SRC_IN);
            fCursorDrawable.set(editor, drawables);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        GradientDrawable gd = new GradientDrawable();
        editText.setBackground(gd);
//        Drawable drawable = editText.getBackground();
//        // 팀트색상을 변경(투명하게), PorterDuff.Mode.SRC_ATOP : Discards the source pixels that do not cover destination pixels.
//        drawable.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_ATOP);
//        editText.setBackground(drawable);
    }

    public String getTextEdit(){
        return editText.getText().toString();
    }

    public EditText getCheckEditText(){
        return editText;
    }

    public Button getCheckButton(){
        return btnDoubleCheck;
    }

    public interface ButtonClickListener{
        void clickEffect();
    }
}