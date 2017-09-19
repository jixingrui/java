package azura.karma.run.bean;

public enum BeanTypeE {
	BOOLEAN, INT, Long, DOUBLE, STRING, BYTES, KARMA, LIST, EMPTY;
	
	public String as3Name() {
		switch (this) {
		case BOOLEAN:
			return "Boolean";
		case INT:
			return "int";
		case Long:
			return "Number";
		case DOUBLE:
			return "Number";
		case STRING:
			return "String";
		case BYTES:
			return "ZintBuffer";
		case KARMA:
			return "Karma";
		case LIST:
			return "KarmaList";
		case EMPTY:
			return "void";
		}
		return "Error";
	}
	
	public String javaName() {
		switch (this) {
		case BOOLEAN:
			return "boolean";
		case INT:
			return "int";
		case Long:
			return "long";
		case DOUBLE:
			return "double";
		case STRING:
			return "String";
		case BYTES:
			return "byte[]";
		case KARMA:
			return "Karma";
		case LIST:
			return "java.util.List<Karma>";
		case EMPTY:
			return "void";
		}
		return "Error";
	}

	public String getterName() {
		switch (this) {
		case BOOLEAN:
			return "Boolean";
		case INT:
			return "Int";
		case Long:
			return "Long";
		case DOUBLE:
			return "Double";
		case STRING:
			return "String";
		case BYTES:
			return "Bytes";
		case KARMA:
			return "Karma";
		case LIST:
			return "List";
		case EMPTY:
			return "void";
		}
		return "Error";
	}
}
