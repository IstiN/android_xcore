package by.istin.android.xcore.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.os.Build;
import android.text.InputType;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import by.istin.android.xcore.callable.ISuccess;
import by.istin.android.xcore.utils.Log;
import by.istin.android.xcore.utils.StringUtil;
import by.istin.android.xcore.utils.UiUtil;

/**
 * @author Uladzimir_Klyshevich
 * Class for building simple dialog.
 */
public class DialogBuilder {

	private static final String OK = "Ok";
	
	private static final String CANCEL = "cancel";
	
	private static final String TAG = DialogBuilder.class.getSimpleName();

	private static int getTheme() {
		if (Build.VERSION.SDK_INT < 11) {
			return android.R.style.Theme_Dialog;
		} else {
			return android.R.style.Theme_Holo_Light_Dialog;
		}
	}
	
	public static Builder createBuilder(final Context context) {
		if (Build.VERSION.SDK_INT < 11) {
			return new Builder(context);
		} else {
			return new Builder(context, getTheme());
		}
	}

	public static void simple(final Context context, String message, final OnClickListener listener) {
		simple(context, null, message, listener);
	}
	
	public static void simple(final Context context, String title, String message, final OnClickListener listener) {
		simple(context, title, message, null, listener);
	}
	
	/**
	 * Create simple dialog.
	 * 
	 * @param context context
	 * @param message message
	 * @param listener listener
	 */
	public static AlertDialog simple(final Context context, String title, String message, String btn, final OnClickListener listener) {
		Builder builder = createBuilder(context);
		if (title != null) {
			builder.setTitle(title);
		}
		builder.setMessage(message);
		builder.setPositiveButton(btn == null ? OK : btn, listener);
		AlertDialog alertDialog = builder.create();
		if (Build.VERSION.SDK_INT > 10) {
			alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		}
		alertDialog.setCancelable(false);
		try {
			alertDialog.show();
			return alertDialog;
		} catch (Exception e) {
			Log.e(TAG, "quick_back", e);
			return null;
		}
	}
	
	public static void confirm(final Context context, String title, String message, final OnClickListener agreeListener) {
		confirm(context, title, message, null, null, null, agreeListener);
	}
	/**
	 * Create confirm dialog.
	 * 
	 * @param context context
	 * @param message message
	 */
	public static void confirm(final Context context, String title, String message, String posBtn, String negBtn, final OnClickListener disagreeListener, final OnClickListener agreeListener) {
		Builder builder = createBuilder(context);
		if (title != null) {
			builder.setTitle(title);
		}
		builder.setMessage(message);
		builder.setPositiveButton(posBtn == null ? OK : posBtn, agreeListener);
		builder.setNegativeButton(negBtn == null ? StringUtil.getStringResource(CANCEL, context) : negBtn, disagreeListener == null ? new OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				try {
					dialog.dismiss();					
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		} : disagreeListener);
		AlertDialog alertDialog = builder.create();
		if (Build.VERSION.SDK_INT > 10) {
			alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		}
		alertDialog.setCancelable(false);
		try {
			alertDialog.show();	
		} catch (Exception e) {
			Log.e(TAG, "quick_back", e);
		}
	}

	public static void options(final Context context, Integer titleResource, int optionsResource, final OnClickListener listener) {
		Builder builder = createBuilder(context);
		if (titleResource != null) {
			builder.setTitle(titleResource);
		}
		int items = optionsResource;
		builder.setItems(items, listener);

		builder.setNegativeButton(StringUtil.getStringResource("cancel", context), new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
			
		});
		try {
			AlertDialog alertDialog = builder.create();
			if (Build.VERSION.SDK_INT > 10) {
				alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
			}
			alertDialog.show();	
		} catch (Exception e) {
			Log.e(TAG, "quick_back", e);
		}
		
	}
	
	public static void options(final Context context, int titleResource, String[] optionsResource, final OnClickListener listener) {
		Builder builder = createBuilder(context);
		builder.setTitle(titleResource);
		builder.setItems(optionsResource, listener);
		builder.setNegativeButton(StringUtil.getStringResource("cancel", context), new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
			
		});
		try {
			AlertDialog alertDialog = builder.create();
			if (Build.VERSION.SDK_INT > 10) {
				alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
			}
			alertDialog.show();	
		} catch (Exception e) {
			Log.e(TAG, "quick_back", e);
		}
		
	}
	
	public static void singleChooseOption(final Context context, int titleResource, int optionsResource, int defaultOption, final OnClickListener listener) {
		Builder builder = createBuilder(context);
		builder.setTitle(titleResource);
		builder.setSingleChoiceItems(optionsResource, defaultOption, listener);
		builder.setNegativeButton(StringUtil.getStringResource("cancel", context), new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
			
		});
		try {
			AlertDialog alertDialog = builder.create();
			if (Build.VERSION.SDK_INT > 10) {
				alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
			}
			alertDialog.show();	
		} catch (Exception e) {
			Log.e(TAG, "quick_back", e);
		}

	}
	
	public static void multiChooseOption(final Context context, int titleResource, int optionsResource, final boolean[] defaultOption, final ISuccess<boolean[]> success) {
        String[] stringArray = context.getResources().getStringArray(optionsResource);
        multiChooseOption(context, titleResource, stringArray, defaultOption, success);
		
	}

    public static void multiChooseOption(Context context, int titleResource, String[] stringArray, final boolean[] defaultOption, final ISuccess<boolean[]> success) {
        Builder builder = createBuilder(context);
        builder.setTitle(titleResource);
        builder.setMultiChoiceItems(stringArray, defaultOption, new OnMultiChoiceClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				defaultOption[which] = isChecked;
			}
		});
        builder.setNegativeButton(StringUtil.getStringResource("cancel", context), new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }

        });
        builder.setPositiveButton(OK, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                success.success(defaultOption);
            }

        });
        try {
            AlertDialog alertDialog = builder.create();
            if (Build.VERSION.SDK_INT > 10) {
                alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            }
            alertDialog.show();
        } catch (Exception e) {
            Log.e(TAG, "quick_back", e);
        }
    }


    public static void input(final Activity activity, String title, String hint, String defaultValue, String positiveButton, boolean isNumber, final ISuccess<String> success) {
		final EditText input = new EditText(activity);
		if (!StringUtil.isEmpty(defaultValue)) {
			input.setText(defaultValue);
		}
		if (isNumber) {
			input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
		}
		MarginLayoutParams marginLayoutParams = new MarginLayoutParams(MarginLayoutParams.MATCH_PARENT, MarginLayoutParams.WRAP_CONTENT);
		marginLayoutParams.leftMargin = UiUtil.getDp(activity, 8);
		marginLayoutParams.rightMargin = UiUtil.getDp(activity, 8);
		input.setLayoutParams(marginLayoutParams);
		input.setHint(hint);
		Builder builder = createBuilder(activity);
		builder.setPositiveButton(positiveButton, new OnClickListener() {
		         public void onClick(DialogInterface dialog, int whichButton) {
		             // deal with the editable
		     		String message = input.getText().toString();
		     		input.setText(StringUtil.EMPTY);
					try {
						InputMethodManager imm = (InputMethodManager)activity.getSystemService(
							      Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
						dialog.dismiss();
					} catch (Exception e) {
						// TODO: handle exception
					}
		     		if (!StringUtil.isEmpty(message)) {
		     			success.success(message);
		     		}
		         }
		    })
		    .setNegativeButton(StringUtil.getStringResource("cancel", activity), new OnClickListener() {
		    	
		         public void onClick(DialogInterface dialog, int whichButton) {
								try {
									InputMethodManager imm = (InputMethodManager)activity.getSystemService(
										      Context.INPUT_METHOD_SERVICE);
									imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
									dialog.dismiss();
								} catch (Exception e) {
									// TODO: handle exception
								}
		         }
		         
		    });
		builder.setTitle(title);
		AlertDialog alertDialog = builder.create();
		int padding = UiUtil.getDp(activity, 10);
		alertDialog.setView(input, padding, padding, padding, padding);
		try {
			if (Build.VERSION.SDK_INT > 10) {
				alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
			}
			alertDialog.show();			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
