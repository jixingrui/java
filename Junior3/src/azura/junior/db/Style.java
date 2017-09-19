package azura.junior.db;

import java.lang.reflect.Field;

public class Style {
	int fontSize = 18;
	// int fontStyle = 0;
	int fontColor = 0;
	// int strokeColor = 0xaaaaaa;
	int fillColor = 0xffffff;

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Field f : getClass().getDeclaredFields()) {
			try {
				// if (f.getName().equals("this$0"))
				// continue;

				sb.append(f.getName()).append("=").append(f.get(this)).append(";");
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
}
