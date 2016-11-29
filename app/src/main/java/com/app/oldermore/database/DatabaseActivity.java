package com.app.oldermore.database;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

@SuppressLint("Instantiatable")
public class DatabaseActivity extends SQLiteOpenHelper {
	private static String DB_NAME = "db_oldster.sqlite";
	private static String TABLE_LOGIN = "tb_login";
	private static Integer BUFFER_SIZE = 128;
	private SQLiteDatabase myDataBase;
	private final Context context;
	private DecimalFormat df = new DecimalFormat("#,###,###.##");
	String message = "";

	public DatabaseActivity(Context context) {
		super(context, DB_NAME, null, 1);
		this.context = context;
	}

	public void openDatabase() {
		File dbFolder = context.getDatabasePath(DB_NAME).getParentFile();
		if (!dbFolder.exists()) {
			dbFolder.mkdir();
		}

		File dbFile = context.getDatabasePath(DB_NAME);

		if (!dbFile.exists()) {
			try {
				copyDatabase(dbFile);
			} catch (IOException e) {
				throw new RuntimeException("Error creating source database", e);
			}
		}
	}

	private void copyDatabase(File targetDbFile) throws IOException {
		// ***********************************************************************************
		// Open your local db as the input stream
		InputStream myInput = context.getAssets().open(DB_NAME);

		// Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(targetDbFile);

		// transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[BUFFER_SIZE];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}

		myOutput.flush();
		myOutput.close();
		myInput.close();

		// ***********************************************************************************
	}

	@Override
	public synchronized void close() {
		if (myDataBase != null)
			myDataBase.close();
		super.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	/*public void InsertLogin(String username, String password, String type) {
		SQLiteDatabase db;
		try {
			db = this.getReadableDatabase(); // Read Data
			String strSQL = "SELECT * FROM " + TABLE_LOGIN +" ";
			Cursor cursor = db.rawQuery(strSQL, null);
			Integer count = cursor.getCount();
			cursor.close();
			db = this.getWritableDatabase(); // Update or Insert to database
			if (count > 0) {
				String strSQLInsert = "DELETE FROM " + TABLE_LOGIN+ " ";
				db.execSQL(strSQLInsert);
			}
			String strSQLInsert = "INSERT INTO " + TABLE_LOGIN
					+ "(username, password, type) VALUES('" + username + "', '" + password
					+ "', '"+type+"') ";
			db.execSQL(strSQLInsert);
			db.close();
		} catch (Exception e) {
			System.out.print(e.getMessage());
		}
	}

	public void DeleleLogin() {
		SQLiteDatabase db;
		try {
			db = this.getWritableDatabase(); // Update Delete or Insert to database
			String strSQLInsert = "DELETE FROM " + TABLE_LOGIN+ " ";
			db.execSQL(strSQLInsert);
			db.close();
		} catch (Exception e) {
			System.out.print(e.getMessage());
		}
	}*/

	public ArrayList<HashMap<String, String>> CheckLogin() {
		try {
			ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> map;
			SQLiteDatabase db;
			db = this.getReadableDatabase(); // Read Data
			String strSQL = "SELECT id, username, password, type FROM "
					+ TABLE_LOGIN+" LIMIT 1 ";
			Cursor cursor = db.rawQuery(strSQL, null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					do {
						map = new HashMap<String, String>();
						/*map.put("id", cursor.getString(0));*/
						map.put("username", cursor.getString(1));
						map.put("password", cursor.getString(2));
						map.put("type", cursor.getString(3));
						MyArrList.add(map);
					} while (cursor.moveToNext());
				}
			}
			cursor.close();
			db.close();
			return MyArrList;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/*public boolean SignIn(String user, String pass) {
		SQLiteDatabase db;
		try {
			db = this.getReadableDatabase(); // Read Data
			String strSQL = "SELECT * FROM " + TABLE_PROFILE + " WHERE user = '"
					+ user + "' AND pass = '" + pass + "' ";
			Cursor cursor = db.rawQuery(strSQL, null);
			Integer count = cursor.getCount();
			cursor.close();
			db.close();
			db = this.getWritableDatabase(); // Update or Insert to database
			if (count > 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			System.out.print(e.toString());
			return false;
		}
	}

	public Boolean SignUp(String user, String pass) {
		SQLiteDatabase db;
		try {
			db = this.getReadableDatabase(); // Read Data
			String strSQL = "SELECT * FROM " + TABLE_PROFILE + " WHERE user = '"
					+ user + "' AND pass = '" + pass + "' ";
			Cursor cursor = db.rawQuery(strSQL, null);
			Integer count = cursor.getCount();
			cursor.close();
			db = this.getWritableDatabase(); // Update or Insert to database
			if (count > 0) {
				db.close();
				return false;
			} else {
				db = this.getWritableDatabase(); // Read Data
				String strSQLInsert = "INSERT INTO " + TABLE_PROFILE
						+ "(user, pass) VALUES('" + user + "', '" + pass
						+ "') ";
				db.execSQL(strSQLInsert);
				db.close();
				return true;
			}
		} catch (Exception e) {
			System.out.print(e.toString());
			return false;
		}
	}

	public void DeleteItem(String item_id) {
		SQLiteDatabase db;
		try {
			db = this.getWritableDatabase(); // Read Data
			String strSQLInsert = "DELETE FROM " + TABLE_REFRIGERATOR
					+ " WHERE id = " + item_id + " ";
			db.execSQL(strSQLInsert);
			db.close();
		} catch (Exception e) {
			System.out.print(e.toString());
		}
	}

	public ArrayList<HashMap<String, String>> SearchItems(String name) {
		try {
			ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> map;
			SQLiteDatabase db;
			db = this.getReadableDatabase(); // Read Data
			String strSQL = "SELECT id, name, amount, unit FROM "
					+ TABLE_REFRIGERATOR + " WHERE name LIKE '%" + name	+ "%' ORDER BY id DESC ";
			Cursor cursor = db.rawQuery(strSQL, null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					do {
						String strAmount = cursor.getString(2);
						if (strAmount != "") {
							strAmount = df
									.format(Double.parseDouble(strAmount))
									.toString().trim();
						}
						map = new HashMap<String, String>();
						map.put("id", cursor.getString(0));
						map.put("name", cursor.getString(1));
						map.put("amount", strAmount);
						map.put("unit", cursor.getString(3));
						MyArrList.add(map);
					} while (cursor.moveToNext());
				}
			}
			cursor.close();
			db.close();
			return MyArrList;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void UpdateItem(String item_id, String strAmount, String strUnit) {
		SQLiteDatabase db;
		try {
			db = this.getWritableDatabase(); // Read Data
			String strSQLInsert = "UPDATE " + TABLE_REFRIGERATOR
					+ " SET amount = " + strAmount + ", unit = '" + strUnit
					+ "' " + "WHERE id = " + item_id + " ";
			db.execSQL(strSQLInsert);
			db.close();
		} catch (Exception e) {
			System.out.print(e.toString());
		}
	}

	public String AddNewItem(String strName, String strAmount, String strUnit) {
		SQLiteDatabase db;
		String message = "";
		try {
			db = this.getReadableDatabase(); // Read Data
			String strSQL = "SELECT * FROM " + TABLE_REFRIGERATOR
					+ " WHERE name = '" + strName + "' ";
			Cursor cursor = db.rawQuery(strSQL, null);
			Integer count = cursor.getCount();
			cursor.close();
			db = this.getWritableDatabase(); // Update or Insert to database
			if (count > 0) {
				db.close();
				message = "This recipe ingredients exist!";
			} else {
				db = this.getWritableDatabase(); // Read Data
				String strSQLInsert = "INSERT INTO " + TABLE_REFRIGERATOR
						+ "(name, amount, unit) VALUES('" + strName + "', "
						+ strAmount + ", '" + strUnit + "') ";
				db.execSQL(strSQLInsert);
				db.close();
				message = "Add ingredients completed";
			}
		} catch (Exception e) {
			System.out.print(e.toString());
			message = e.getMessage();
		}
		return message;
	}

	public ArrayList<HashMap<String, String>> SearchRecipes(String v_name) {
		try {
			ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> map;
			SQLiteDatabase db;
			db = this.getReadableDatabase(); // Read Data
			String strSQL = "SELECT r.id, r.name, r.calories, s.name, r.how_to_cook FROM "
					+ TABLE_RECIPE + " r LEFT OUTER JOIN " + TABLE_SPECIFIC + " s ON r.specific_id = s.id WHERE r.name LIKE '%" + v_name + "%' "
					+ " ORDER BY r.calories ";
			Cursor cursor = db.rawQuery(strSQL, null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					do {
						map = new HashMap<String, String>();
						map.put("id", cursor.getString(0));
						map.put("name", cursor.getString(1));
						map.put("calories", "Calories = " + (df.format(Double.parseDouble(cursor.getString(2)))).toString());
						map.put("specific_name", cursor.getString(3));
						map.put("how_to_cook", cursor.getString(4));
						MyArrList.add(map);
					} while (cursor.moveToNext());
				}
			}
			cursor.close();
			db.close();
			return MyArrList;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String AddNewRecipe(String strName, String strCalories, String specificId) {
		SQLiteDatabase db;
		String statusMsg = "";
		try {
			db = this.getReadableDatabase(); // Read Data
			String strSQL = "SELECT * FROM " + TABLE_RECIPE
					+ " WHERE name = '" + strName + "' ";
			Cursor cursor = db.rawQuery(strSQL, null);
			Integer count = cursor.getCount();
			cursor.close();
			if (count > 0) {
				db.close();
				statusMsg = "This recipe already exist!";
			} else {
				db = this.getWritableDatabase(); // Update or Insert to database
				String strSQLInsert = "INSERT INTO " + TABLE_RECIPE
						+ "(name, calories, specific_id) VALUES('" + strName + "', "
						+ strCalories + ", " + specificId + ") ";
				db.execSQL(strSQLInsert);
				db.close();
				statusMsg = "Add recipe completed";
			}
		} catch (Exception e) {
			System.out.print(e.getMessage());
			statusMsg = "Error : " + e.getMessage();
		}
		return statusMsg;
	}

	public ArrayList<HashMap<String, String>> GetSpecific() {
		try {
			ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> map;
			SQLiteDatabase db;
			db = this.getReadableDatabase(); // Read Data
			String strSQL = "SELECT id, name FROM "
					+ TABLE_SPECIFIC + " ORDER BY id ";
			Cursor cursor = db.rawQuery(strSQL, null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					do {
						map = new HashMap<String, String>();
						map.put("id", cursor.getString(0));
						map.put("name", cursor.getString(1));
						MyArrList.add(map);
					} while (cursor.moveToNext());
				}
			}
			cursor.close();
			db.close();
			return MyArrList;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String EditRecipe(String e_recipe_id, String strName, String strCalories, String specificId, String strHowToCook) {
		SQLiteDatabase db;
		String statusMsg = "";
		try {
			db = this.getWritableDatabase(); // Update or Insert to database
			String strSQLInsert = "UPDATE " + TABLE_RECIPE + " SET "
					+ " name = '" + strName + "', calories = " + strCalories + " , specific_id = " + specificId + ", how_to_cook = '" + strHowToCook + "' WHERE id = " + e_recipe_id + " ";
			db.execSQL(strSQLInsert);
			db.close();
			statusMsg = "Change recipe completed";
		} catch (Exception e) {
			System.out.print(e.getMessage());
			statusMsg = "Error : " + e.getMessage();
		}
		return statusMsg;
	}

	public void SaveInDB(String selectedImagePath, String e_recipe_id) {
		byte[] byteImage = null;
		SQLiteDatabase db;
		db = this.getWritableDatabase();
		ContentValues Val = new ContentValues();
		try {
			FileInputStream instream = new FileInputStream(selectedImagePath);
			BufferedInputStream bif = new BufferedInputStream(instream);
			byteImage = new byte[bif.available()];
			bif.read(byteImage);
			Val.put("image", byteImage);
			long ret = db.update(TABLE_RECIPE, Val, " id = ?",
					new String[]{String.valueOf(e_recipe_id)});
			if (ret < 0) {
				System.out.print("Error");
			} else {
				System.out.print("Success");
			}
		} catch (Exception e) {
			System.out.print(e.toString());
		}
		db.close();
	}

	public byte[] ReadImageFromDB(String e_recipe_id) {
		byte[] byteImage = null;
		SQLiteDatabase db;
		db = this.getReadableDatabase();
		try {
			String strSQL = "SELECT * FROM " + TABLE_RECIPE
					+ " WHERE id = '" + e_recipe_id + "' LIMIT 1 ";
			Cursor cursor = db.rawQuery(strSQL, null);

			cursor.moveToFirst();
			while (cursor.isAfterLast() == false) {
				System.out.println("\n Reading Details \n Name : "
						+ cursor.getString(1));
				cursor.moveToNext();
			}
			// /////Read data from blob field////////////////////
			cursor.moveToFirst();
			byteImage = cursor.getBlob(cursor.getColumnIndex("image"));
			cursor.close();
			db.close();
			return byteImage;
		} catch (Exception e) {
			System.out.print(e.toString());
			db.close();
			return null;
		}
	}

	public void DeleteRecipe(String recipe_id) {
		SQLiteDatabase db;
		try {
			db = this.getWritableDatabase(); // Read Data
			String strSQLRe = "DELETE FROM " + TABLE_RECIPE
					+ " WHERE id = " + recipe_id + " ";
			db.execSQL(strSQLRe);
			String strSQLReAddItems = "DELETE FROM " + TABLE_RECIPE_ADD_ITEMS
					+ " WHERE recipe_id = " + recipe_id + " ";
			db.execSQL(strSQLReAddItems);
			db.close();
		} catch (Exception e) {
			System.out.print(e.toString());
		}
	}

	public ArrayList<HashMap<String, String>> SearchItemsInRecipe(String recipe_id) {
		try {
			ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> map;
			SQLiteDatabase db;
			db = this.getReadableDatabase(); // Read Data
			String strSQL = "SELECT a.refrigerator_id, r.name, a.amount, r.unit FROM " + TABLE_RECIPE_ADD_ITEMS + " a LEFT OUTER JOIN "
					+ TABLE_REFRIGERATOR + " r ON a.refrigerator_id = r.id WHERE a.recipe_id = " + recipe_id + " ";
			Cursor cursor = db.rawQuery(strSQL, null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					do {
						String strAmount = cursor.getString(2);
						if (strAmount != "") {
							strAmount = df
									.format(Double.parseDouble(strAmount))
									.toString().trim();
						}
						map = new HashMap<String, String>();
						map.put("id", cursor.getString(0));
						map.put("name", cursor.getString(1));
						map.put("amount", strAmount);
						map.put("unit", cursor.getString(3));
						MyArrList.add(map);
					} while (cursor.moveToNext());
				}
			}
			cursor.close();
			db.close();
			return MyArrList;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String InsertRecipeAddItems(String recipe_id, String item_id, String amount) {
		SQLiteDatabase db;
		String statusMsg = "";
		try {
			db = this.getReadableDatabase(); // Read Data
			String strSQL = "SELECT * FROM " + TABLE_RECIPE_ADD_ITEMS
					+ " WHERE recipe_id = " + recipe_id + " AND refrigerator_id = " + item_id + " ";
			Cursor cursor = db.rawQuery(strSQL, null);
			Integer count = cursor.getCount();
			cursor.close();
			if (count > 0) {
				db.close();
				statusMsg = "This ingredients already exist!";
			} else {
				db = this.getWritableDatabase(); // Update or Insert to database
				String strSQLInsert = "INSERT INTO " + TABLE_RECIPE_ADD_ITEMS
						+ "(recipe_id, refrigerator_id, amount) VALUES('" + recipe_id + "', "
						+ item_id + ", " + amount + ") ";
				db.execSQL(strSQLInsert);
				db.close();
				statusMsg = "Add ingredients completed";
			}
		} catch (Exception e) {
			System.out.print(e.getMessage());
			statusMsg = "Error : " + e.getMessage();
		}
		return statusMsg;
	}

	public void DeleteItemInRecipe(String recipe_id, String item_id) {
		SQLiteDatabase db;
		try {
			db = this.getWritableDatabase(); // Read Data
			String strSQLInsert = "DELETE FROM " + TABLE_RECIPE_ADD_ITEMS
					+ " WHERE recipe_id = " + recipe_id + " AND refrigerator_id = " + item_id + " ";
			db.execSQL(strSQLInsert);
			db.close();
		} catch (Exception e) {
			System.out.print(e.toString());
		}
	}

	public void UpdateItemInRecipe(String recipe_id, String item_id, String strAmount) {
		SQLiteDatabase db;
		try {
			db = this.getWritableDatabase(); // Read Data
			String strSQLInsert = "UPDATE " + TABLE_RECIPE_ADD_ITEMS
					+ " SET amount = " + strAmount
					+ " WHERE recipe_id = " + recipe_id + " AND refrigerator_id = " + item_id + " ";
			db.execSQL(strSQLInsert);
			db.close();
		} catch (Exception e) {
			System.out.print(e.toString());
		}
	}

	public ArrayList<HashMap<String, String>> SearchRecipesBySpecificId(String searchSpecificId, String name) {
		//String[] spSpecificId = searchSpecificId.split(",");
		try {
			ArrayList<HashMap<String, String>> RecipeAllList = new ArrayList<HashMap<String, String>>();
			ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> map;
			SQLiteDatabase db;
			db = this.getReadableDatabase(); // Read Data

			String strSQLRecipeAll = "SELECT rai.recipe_id, COUNT(rai.recipe_id) AS count FROM " + TABLE_RECIPE_ADD_ITEMS + " rai "
					+ " LEFT OUTER JOIN " + TABLE_RECIPE + " r ON rai.recipe_id = r.id"
					+ " WHERE r.name LIKE '%" + name + "%' "+searchSpecificId+ " "
					+ " GROUP BY rai.recipe_id"
					+ " ORDER BY rai.id ";
			Cursor cursorRecipeAll = db.rawQuery(strSQLRecipeAll, null);
			if (cursorRecipeAll != null) {
				if (cursorRecipeAll.moveToFirst()) {
					do {
						map = new HashMap<String, String>();
						map.put("recipe_id", cursorRecipeAll.getString(0));
						map.put("count", cursorRecipeAll.getString(1));
						RecipeAllList.add(map);
					} while (cursorRecipeAll.moveToNext());
				}
			}

			for (int i = 0; i < RecipeAllList.size(); i++) {
				String strSQLInRefrigerator = "SELECT rai.recipe_id FROM " + TABLE_RECIPE_ADD_ITEMS + " rai "
						+ " LEFT OUTER JOIN " + TABLE_REFRIGERATOR + " ref ON rai.refrigerator_id = ref.id"
						+ " WHERE recipe_id = " + RecipeAllList.get(i).get("recipe_id")
						+ " AND  ref.amount >= rai.amount";
				Cursor cursorInRefrigerator = db.rawQuery(strSQLInRefrigerator, null);
				Integer countInRefrigerator = cursorInRefrigerator.getCount();
				cursorInRefrigerator.close();
				if (Integer.parseInt(RecipeAllList.get(i).get("count")) == countInRefrigerator) {
					String strSQL = "SELECT r.id, r.name, r.calories, s.name, r.how_to_cook FROM "
							+ TABLE_RECIPE + " r LEFT OUTER JOIN " + TABLE_SPECIFIC + " s ON r.specific_id = s.id WHERE r.id = " + RecipeAllList.get(i).get("recipe_id")
							+ " ORDER BY r.calories LIMIT 1";
					Cursor cursor = db.rawQuery(strSQL, null);
					if (cursor != null) {
						if (cursor.moveToFirst()) {
							do {
								map = new HashMap<String, String>();
								map.put("id", cursor.getString(0));
								map.put("name", cursor.getString(1));
								map.put("calories", "Calories = " + (df.format(Double.parseDouble(cursor.getString(2)))).toString());
								map.put("specific_name", cursor.getString(3));
								map.put("how_to_cook", cursor.getString(4));
								MyArrList.add(map);
							} while (cursor.moveToNext());
						}
					}
					cursor.close();
				}
			}
			db.close();
			return MyArrList;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String MakeADish(String recipe_id, ArrayList<HashMap<String, String>> refrigeratorList) {
		SQLiteDatabase db = null;
		Double dAmount = 0.00;
		String message = "";
		try {
			if(refrigeratorList != null){
				for (int i = 0; i < refrigeratorList.size(); i++) {
					dAmount = 0.00;
					db = this.getReadableDatabase(); // Read Data
					String strSQL = "SELECT id, name, amount, unit FROM "
							+ TABLE_REFRIGERATOR + " WHERE id = " + refrigeratorList.get(i).get("id") + " LIMIT 1";
					Cursor cursor = db.rawQuery(strSQL, null);
					if (cursor != null) {
						if (cursor.moveToFirst()) {
							do {
							if(cursor.getString(2) != null && refrigeratorList.get(i).get("amount") != null){
								dAmount = Double.parseDouble(cursor.getString(2).toString().trim()) - Double.parseDouble(refrigeratorList.get(i).get("amount").toString().trim());
							}
							db = this.getWritableDatabase(); // Write Data
							String strSQLUpdate = "UPDATE " + TABLE_REFRIGERATOR
									+ " SET amount = " + dAmount
									+ " WHERE id = " + refrigeratorList.get(i).get("id") + " ";
							db.execSQL(strSQLUpdate);
							} while (cursor.moveToNext());
						}
					}
					cursor.close();
				}
				db = this.getWritableDatabase(); // Write Data
				String strSQLUpdate = "INSERT INTO " + TABLE_HISTORY
						+ "(recipe_id) VALUES(" + recipe_id + ") ";
				db.execSQL(strSQLUpdate);

				message = "Make a dish completed";
			}
			else{
				message = "Can not make a dish";
			}
		}
		catch (Exception e) {
			System.out.print(e.toString());
			message = e.getMessage().toString();
		}
		db.close();
		return message;
	}

	public ArrayList<HashMap<String, String>> SearchHistory(String dateFrom, String dateTo) {
		try {
			ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> map;
			SQLiteDatabase db;
			db = this.getReadableDatabase(); // Read Data
			String strSQL = "SELECT r.name, r.calories, h.date_time FROM "
					+ TABLE_HISTORY + " h LEFT OUTER JOIN " + TABLE_RECIPE + " r ON h.recipe_id = r.id "
					+ " WHERE (h.date_time BETWEEN '"+dateFrom+"' AND '"+dateTo+"') "
					+ " ORDER BY h.date_time DESC";
			Cursor cursor = db.rawQuery(strSQL, null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					do {
						map = new HashMap<String, String>();
						map.put("name", cursor.getString(0));
						map.put("calories", "Calories = " + (df.format(Double.parseDouble(cursor.getString(1)))).toString());
						map.put("date_time", cursor.getString(2));
						MyArrList.add(map);
					} while (cursor.moveToNext());
				}
			}
			cursor.close();
			db.close();
			return MyArrList;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	public String IdOfIngredientsName(String strName) {
		String str_id = "";
		try {
			ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> map;
			SQLiteDatabase db;
			db = this.getReadableDatabase(); // Read Data
			String strSQL = "SELECT id FROM "+TABLE_REFRIGERATOR+" WHERE name = '"+strName+"' LIMIT 1 ";
			Cursor cursor = db.rawQuery(strSQL, null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					do {
						map = new HashMap<String, String>();
						map.put("id", cursor.getString(0));
						MyArrList.add(map);
					} while (cursor.moveToNext());
				}
			}
			cursor.close();
			db.close();
			str_id = MyArrList.get(0).get("id");
			return str_id;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}*/
}
