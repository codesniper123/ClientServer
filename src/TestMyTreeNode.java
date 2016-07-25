import static org.junit.Assert.*;

import org.junit.Test;

public class TestMyTreeNode {

	@Test
	public void test() {
		MyTreeNode<Integer> tree = new MyTreeNode<Integer>(1);
		MyTreeNode<Integer> two = tree.addChild(2);
		tree.addChild(3);
		tree.addChild(4);
		two.addChild(5);
		two.addChild(6);
		two.addChild(7);
		
		tree.printPreOrder();
	}
}
