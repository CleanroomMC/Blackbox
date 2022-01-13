package io.github.cleanroommc.blackbox.utils;

import com.google.common.base.MoreObjects;

import java.util.Objects;

public class Vec2i {

	public static final Vec2i ORIGIN = new Vec2i(0, 0);

	public final int x;
	public final int y;

	public Vec2i(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Vec2i add(Vec2i other) {
		return new Vec2i(x + other.x, y + other.y);
	}

	public Vec2i subtract(Vec2i other) {
		return new Vec2i(x - other.x, y - other.y);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Vec2i)) {
			return false;
		}
		Vec2i Vec2i = (Vec2i) o;
		return x == Vec2i.x && y == Vec2i.y;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("x", x).add("y", y).toString();
	}

}
