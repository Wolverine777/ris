package app.datatype;

/**
 * @author Benjamin Reemts
 *
 */

public class FontInfo {
	//Dont make Setters
	private int size, style;
	private String name;
	public FontInfo(int size, int style, String name) {
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
