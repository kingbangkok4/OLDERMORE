package com.app.oldermore.imageprocessing;

import android.graphics.Bitmap;

public interface ITransformation {
	Bitmap perform(Bitmap inp);
}
