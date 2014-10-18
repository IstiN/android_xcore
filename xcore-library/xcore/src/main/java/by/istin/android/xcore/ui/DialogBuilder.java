package by.istin.android.xcore.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.os.Build;
import android.os.Handler;
import android.text.InputType;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.lang.reflect.Field;

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

    @TargetApi(value = Build.VERSION_CODES.LOLLIPOP)
	private static int getTheme() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			return android.R.style.Theme_Dialog;
		} else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT_WATCH ) {
            return android.R.style.Theme_DeviceDefault_Dialog;
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
			return android.R.style.Theme_Holo_Light_Dialog;
		} else {
            try {
                return android.R.style.Theme_Material_Light_Dialog;
            } catch (Exception e) {
                return android.R.style.Theme_Holo_Light_Dialog;
            }
        }
	}

    @TargetApi(value = Build.VERSION_CODES.HONEYCOMB)
	public static Builder createBuilder(final Context context) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
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
        applyBackground(alertDialog);
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
					// quick back issue for old android version
                    Log.e("DialogBuilder", e);
				}
			}
		} : disagreeListener);
		AlertDialog alertDialog = builder.create();
        applyBackground(alertDialog);
        alertDialog.setCancelable(false);
		try {
			alertDialog.show();	
		} catch (Exception e) {
			Log.e(TAG, "quick_back", e);
		}
	}

    public static void applyBackground(AlertDialog alertDialog) {
        if (Build.VERSION.SDK_INT > 10) {
            if (!UiUtil.hasL()) {
                alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            }
        }
    }

    public static void options(final Context context, Integer titleResource, int optionsResource, final OnClickListener listener) {
		Builder builder = createBuilder(context);
		if (titleResource != null) {
			builder.setTitle(titleResource);
		}
		builder.setItems(optionsResource, listener);

		builder.setNegativeButton(StringUtil.getStringResource("cancel", context), new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
			
		});
		try {
			AlertDialog alertDialog = builder.create();
            applyBackground(alertDialog);
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
            applyBackground(alertDialog);
            alertDialog.show();
		} catch (Exception e) {
			Log.e(TAG, "quick_back", e);
		}
		
	}
	
	public static void singleChooseOption(final Context context, int titleResource, int optionsResource, int defaultOption, final OnClickListener listener) {
        String[] stringArray = context.getResources().getStringArray(optionsResource);
        singleChooseOption(context, titleResource, stringArray, defaultOption, listener);

	}
    public static void singleChooseOption(Context context, int titleResource, String[] stringArray, int defaultOption, OnClickListener listener) {
        singleChooseOption(context, context.getString(titleResource), stringArray, defaultOption, null, listener);
    }

    public static void singleChooseOption(Context context, String[] stringArray, int defaultOption, String closeButton, OnClickListener listener) {
        singleChooseOption(context, null, stringArray, defaultOption, closeButton, listener);
    }

    public static void singleChooseOption(Context context, String titleResource, String[] stringArray, int defaultOption, String closeButton, OnClickListener listener) {
        Builder builder = createBuilder(context);
        if (!StringUtil.isEmpty(titleResource)) {
            builder.setTitle(titleResource);
        }
        builder.setSingleChoiceItems(stringArray, defaultOption, listener);
        if (StringUtil.isEmpty(closeButton)) {
            closeButton = StringUtil.getStringResource("cancel", context);
        }
        builder.setNegativeButton(closeButton, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }

        });
        try {
            AlertDialog alertDialog = builder.create();
            applyBackground(alertDialog);
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
            applyBackground(alertDialog);
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
			input.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            input.setSingleLine(true);
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
                        Log.e("dialog", e);
						//quick back issue
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
                                    Log.e("dialog", e);
                                    // quick back issue for old android version
								}
		         }
		         
		    });
		builder.setTitle(title);
		AlertDialog alertDialog = builder.create();
		int padding = UiUtil.getDp(activity, 10);
		alertDialog.setView(input, padding, padding, padding, padding);
		try {
            applyBackground(alertDialog);
            alertDialog.show();
		} catch (Exception e) {
            // quick back issue for old android version
            Log.e("DialogBuilder", e);
		}
        input.setSelection(input.getText().length());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                UiUtil.showKeyboard(input);
            }
        }, 200l);
	}
}
