package pad.stand.com.haidiyun.www.widget;

import android.widget.GridView;
import android.widget.ListView;
import android.content.Context; 
import android.util.AttributeSet; 
public class TGridView extends GridView{ 
 
 
	public TGridView(Context context) { 
        super(context); 
        // TODO Auto-generated constructor stub 
    } 
    public TGridView(Context context, AttributeSet attrs) { 
        super(context, attrs); 
        // TODO Auto-generated constructor stub 
    } 
    public TGridView(Context context, AttributeSet attrs, int defStyle) { 
        super(context, attrs, defStyle); 
        // TODO Auto-generated constructor stub 
    } 
    @Override 
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) { 

        int expandSpec = MeasureSpec.makeMeasureSpec(  
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);  
         
        super.onMeasure(widthMeasureSpec, expandSpec); 
    } 
 
} 