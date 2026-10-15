package com.phys;

public class AABB {
	private final double epsilon = 0.0F;
	
	public double x0;
	public double y0;
	public double z0;
	public double x1;
	public double y1;
	public double z1;
	
	public AABB(double x0, double y0, double z0, double x1, double y1, double z1) {
		this.x0 = x0;
		this.y0 = y0;
		this.z0 = z0;
		this.x1 = x1;
		this.y1 = y1;
		this.z1 = z1;
	}
	
	public AABB clone() {
		return new AABB(x0, y0, z0, x1, y1, z1);
	}
	
	public AABB expand(double x, double y, double z) {
		double x0 = this.x0;
		double y0 = this.y0;
		double z0 = this.z0;
		double x1 = this.x1;
		double y1 = this.y1;
		double z1 = this.z1;
		
		if(x < 0.0F) {
			x0 += x;
		}else {
			x1 += x;
		}
		
		if(y < 0.0F) {
			y0 += y;
		}else {
			y1 += y;
		}
		
		if(z < 0.0F) {
			z0 += z;
		}else {
			z1 += z;
		}
		
		return new AABB(x0, y0, z0, x1, y1, z1);
	}
	
	public AABB grow(double x, double y, double z) {
		return new AABB(x0 - x, y0 - y, z0 - z, x1 + x, y1 + y, z1 + z);
	}
	
	public double clipXCollide(AABB otherBoundingBox, double x) {
		if(otherBoundingBox.y1 <= y0 || otherBoundingBox.y0 >= y1) {
			return x;
		}
		if(otherBoundingBox.z1 <= z0 || otherBoundingBox.z0 >= z1) {
			return x;
		}
		if(x > 0.0F && otherBoundingBox.x1 <= x0) {
			double max = x0 - otherBoundingBox.x1 - epsilon;
			if(max < x) {
				x = max;
			}
		}
		if(x < 0.0F && otherBoundingBox.x0 >= x1) {
			double max = x1 - otherBoundingBox.x0 + epsilon;
			if(max > x) {
				x = max;
			}
		}
		
		return x;
	}
	
	public double clipYCollide(AABB otherBoundingBox, double y) {
		if(otherBoundingBox.x1 <= x0 || otherBoundingBox.x0 >= x1) {
			return y;
		}
		if(otherBoundingBox.z1 <= z0 || otherBoundingBox.z0 >= z1) {
			return y;
		}
		if(y > 0.0F && otherBoundingBox.y1 <= y0) {
			double max = y0 - otherBoundingBox.y1 - epsilon;
			if(max < y) {
				y = max;
			}
		}
		if(y < 0.0F && otherBoundingBox.y0 >= y1) {
			double max = y1 - otherBoundingBox.y0 + epsilon;
			if(max > y) {
				y = max;
			}
		}
		
		return y;
	}
	
	public double clipZCollide(AABB otherBoundingBox, double z) {
		if(otherBoundingBox.x1 <= x0 || otherBoundingBox.x0 >= x1) {
			return z;
		}
		if(otherBoundingBox.y1 <= y0 || otherBoundingBox.y0 >= y1) {
			return z;
		}
		if(z > 0.0F && otherBoundingBox.z1 <= z0) {
			double max = z0 - otherBoundingBox.z1 - epsilon;
			if(max < z) {
				z = max;
			}
		}
		if(z < 0.0F && otherBoundingBox.z0 >= z1) {
			double max = z1 - otherBoundingBox.z0 + epsilon;
			if(max > z) {
				z = max;
			}
		}
		
		return z;
	}
	
	public boolean intersects(AABB otherBoundingBox) {
		if(otherBoundingBox.x1 <= x0 || otherBoundingBox.x0 >= x1) {
			return false;
		}
		if(otherBoundingBox.y1 <= y0 || otherBoundingBox.y0 >= y1) {
			return false;
		}
		
		return (!(otherBoundingBox.z1 <= z0)) && (!(otherBoundingBox.z0 >= z1));
	}
	
	public void move(double x, double y, double z) {
		x0 += x;
		y0 += y;
		z0 += z;
		x1 += x;
		y1 += y;
		z1 += z;
	}
	
	public AABB offset(double x, double y, double z) {
		return new AABB(x0 + x, y0 + y, z0 + z, x1 + x, y1 + y, z1 + z);
	}

}
