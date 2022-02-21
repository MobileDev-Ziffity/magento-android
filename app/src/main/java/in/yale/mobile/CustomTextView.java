package in.yale.mobile;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class CustomTextView extends AppCompatTextView {
    public CustomTextView(Context context) {
        super(context);
        init(context, (AttributeSet) null, 0);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        if (attrs != null) {
            try {
                TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomTextView);
                String fontName = typedArray.getString(R.styleable.CustomTextView_typefamily);
                if (fontName != null) {
                    Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/"+fontName);
                    setTypeface(typeface);
                }
                typedArray.recycle();
            } catch (Exception unused) { }
        }
    }
}
