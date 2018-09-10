package pad.stand.com.haidiyun.www.widget;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import pad.stand.com.haidiyun.www.R;

/**
 * @author
 */
public class SettingMM1Dialog extends IDialog
{

	ImageView close;
	ImageView cancel;

	TextView tvTip, tvTitle;

	Button btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9;

	Button sure;
	private String password = "";

	public SettingMM1Dialog(Context context)
	{
		super(context, R.style.selectorDialog);
		intiDialog(context);
	}

	private void intiDialog(Context context)
	{
		 this.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
		View view = null;
		view = LayoutInflater.from(context)
				.inflate(R.layout.set_mima_dl1, null);
		setContentView(view);
		close = (ImageView) view.findViewById(R.id.set_ml_dl1_close);
		cancel = (ImageView) view.findViewById(R.id.set_ml_dl1_cancel);
		tvTip = (TextView) view.findViewById(R.id.set_ml_dl1_tv);
		btn1 = (Button) view.findViewById(R.id.set_num_btn1);
		btn2 = (Button) view.findViewById(R.id.set_num_btn2);
		btn3 = (Button) view.findViewById(R.id.set_num_btn3);
		btn4 = (Button) view.findViewById(R.id.set_num_btn4);
		btn5 = (Button) view.findViewById(R.id.set_num_btn5);
		btn6 = (Button) view.findViewById(R.id.set_num_btn6);
		btn7 = (Button) view.findViewById(R.id.set_num_btn7);
		btn8 = (Button) view.findViewById(R.id.set_num_btn8);
		btn9 = (Button) view.findViewById(R.id.set_num_btn9);

		sure = (Button) view.findViewById(R.id.set_mm_dl1_yes);
		//
		close.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				dismiss();
			}
		});

		btn1.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				if (password.length() == 4)
				{
					return;
				}
				password += "1";
				tvTip.setText(password);
			}
		});
		btn2.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				if (password.length() == 4)
				{
					return;
				}
				password += "2";
				tvTip.setText(password);
			}
		});
		btn3.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				if (password.length() == 4)
				{
					return;
				}
				password += "3";
				tvTip.setText(password);
			}
		});
		btn4.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				if (password.length() == 4)
				{
					return;
				}
				password += "4";
				tvTip.setText(password);
			}
		});
		btn5.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				if (password.length() == 4)
				{
					return;
				}
				password += "5";
				tvTip.setText(password);
			}
		});
		btn6.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				if (password.length() == 4)
				{
					return;
				}
				password += "6";
				tvTip.setText(password);
			}
		});
		btn7.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				if (password.length() == 4)
				{
					return;
				}
				password += "7";
				tvTip.setText(password);
			}
		});
		btn8.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				if (password.length() == 4)
				{
					return;
				}
				password += "8";
				tvTip.setText(password);
			}
		});
		btn9.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				if (password.length() == 4)
				{
					return;
				}
				password += "9";
				tvTip.setText(password);
			}
		});

		cancel.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				if (password.length() > 0)
				{
					password = password.substring(0, password.length() - 1);
				}
				tvTip.setText(password);
			}
		});
	}


	public ImageView getCloseImg()
	{
		return close;
	}


	public void setTip(String str)
	{
		tvTip.setText(str);
	}


	public Button getSureBtn()
	{
		return sure;
	}


	public String getPassword()
	{
		return password;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev)
	{
		switch (ev.getAction())
		{
		case MotionEvent.ACTION_DOWN:

			Intent intent = new Intent();
			intent.setAction("org.tuch.number");
			getContext().sendBroadcast(intent);
			break;

		default:
			break;
		}
		return super.dispatchTouchEvent(ev);
	}
}
