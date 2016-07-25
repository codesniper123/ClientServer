import java.util.Iterator;
import java.util.LinkedList;

public class MyTreeNode<T> {
	protected T data;
	protected LinkedList<MyTreeNode<T>> children;
	
	public MyTreeNode(T data) {
		this.data = data;
		children = new LinkedList<MyTreeNode<T>>();
	}
	
	public T getData() { return data; }
	
	public MyTreeNode<T> addChild(T data) {
		MyTreeNode<T> child = new MyTreeNode<T>(data);
		children.add(child);
		return child;
	}
	
	public MyTreeNode<T> addChild(MyTreeNode<T> child) {
		children.add(child);
		return child;
	}
	
	public void printPreOrder() {
		printPreOrder(0);
	}
	
	private void printPreOrder(int level) {
		for( int i = 0; i < level; i++ ) 
			System.out.printf( "  " );
		System.out.println(data);
		for( Iterator<MyTreeNode<T>> it = children.iterator(); it.hasNext(); ) {
			it.next().printPreOrder(level+1);
		}
	}
}
