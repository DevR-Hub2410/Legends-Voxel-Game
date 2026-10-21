package com.level;

public interface LevelListener {
	void lightColumnChanged(int x, int z, int y0, int y1);
	void tileChanged(int x, int y, int z);
	void allChanged();

}
