package app.datatype;

/**
 * @author Benjamin Reemts
 *
 */

public class FontInfo {
	//Dont make Setters
	private final int size, style;
	private final String name;
	public FontInfo(String name, int style, int size) {
		this.size = size;
		this.style = style;
		this.name = name;
	}
	public int getSize() {
		return size;
	}
	public int getStyle() {
		return style;
	}
	public String getName() {
		return name;
	}
	
}
