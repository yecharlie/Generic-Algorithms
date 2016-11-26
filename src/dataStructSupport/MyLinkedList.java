package dataStructSupport;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**这里实现的是非循环单链表，不带头结点。除头指针外，还附带
  	*尾指针。*/
public class MyLinkedList<E> extends MyAbstractList<E> {

	//头指针
	private Node<E> head;
	
	//尾指针
	private Node<E>tail;
	
	/**构造函数，建立一个空链表*/
	public MyLinkedList(){		
	}
	
	/**构造函数，建立一个链表，并按序将给定数组中的元素添加到链表中*/	
	public MyLinkedList(E[] objects){	
		for(int i = 0;i < objects.length;i ++)
			addLast(objects[i]);
	}
	
	/**主函数*/
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String[] fruits = new String[3];
		fruits[0] = new String("香蕉");
		fruits[1] = new String("葡萄");
		fruits[2] = new String("西瓜");
		MyLinkedList<String> list = new MyLinkedList<String>(fruits);
		System.out.println("[1] " + list);
		list.addFirst(new String("鸡蛋"));
		System.out.println("[2] " + list);
		list.add(2,new String("鸡蛋"));
		System.out.println("[3] " + list 
				+" 其中第1、4个和最后一个元素分别是 " 
				+list.getFirst()+"、"+  list.get(3)+"、" + list.getLast());
		System.out.println("[4] " + "第一个鸡蛋位置: "+list.indexOf("鸡蛋")
		+" 最后一个鸡蛋位置："+list.lastIndexOf("鸡蛋") );
		System.out.println("[5] " + "老板，有西瓜吗？" + list.contains("西瓜")
		+" 那您这里有麻婆豆腐吗？" + list.contains("麻婆豆腐"));
		System.out.println("[6] " + "前两个和最后一个东西我要了");
		list.remove(1);
		System.out.println( list);
		list.removeFirst();
		System.out.println( list);
		list.removeLast();
		System.out.println( list);
		System.out.println("[7] " + "我这里有麻婆豆腐，跟您换个鸡蛋");
		String egg = list.set(0, "麻婆豆腐");
		System.out.println( list + " 我获得了：" +egg);
		Iterator<String> iter = list.iterator();
		System.out.println("[8] " + "遍历： ");
		while(iter.hasNext())
			System.out.print(iter.next() + " ");
		
	}

	/**将元素e添加到链表表头。
	 * @param e 待插入的元素*/
	public void addFirst(E e){
		add(0,e);
	}
	
	/**将元素插入链表表尾
	 * @param e 待插入元素*/
	public void addLast(E e){
		add(size,e);
	}
	
	
	/**将元素插入到链表制定位置
	 * @param index 元素插入到链表中的位置
	 * @param e 待插入的元素
	 * @throws IndexOutOfBoundsException 如果下标超出范围（index < 0 || index > size()）*/
	@Override
	public void add(int index, E e) {
		// TODO Auto-generated method stub
		checkPositionIndex(index);
		
		//创建新结点
		Node<E> newNode = new Node<E>(e);
		
		//按情况插入结点
		if(isEmpty()){
			head = tail = newNode;
			newNode.next = null;
		}else if(index == 0){
			newNode.next = head;
			head = newNode;
		}else if(index == size){
			tail.next = newNode;
			tail = newNode;
		}else{//插入中间。事实上，此时size >= 2
			Node<E> current = head;
			
			//找到index - 1位置上的结点
			for(int i = 0;i < index - 1;i ++)
				current = current.next;
			
			newNode.next = current.next;
			current.next = newNode;
		}
		//最后，元素数目加1
		size ++;
	}
	
	/**将链表清空*/
	@Override
	public void clear() {
		// TODO Auto-generated method stub
		//不必一边遍历链表，一边“释放”结点空间
		head = tail = null;			
		
		//记得元素数目归0
		size = 0;
	}


	@Override
	public boolean contains(E e) {
		// TODO Auto-generated method stub		
		return indexOf(e) != -1;
	}
	
	/**获取该链表的第一个元素，或者抛出异常
	 * @return 如果链表不空，返回该链表的第一个元素
	 * @throws NoSuchElementException 链表为空*/
	public E getFirst(){
		try{
			return get(0);
		}catch(IndexOutOfBoundsException e){
			throw new NoSuchElementException(); 
		}
	}
	
	/**获取该链表最后一个素，或者抛出异常
	 * @return 如果链表不空，返回该链表最后一个元素
	 * @throws NoSuchElementException 链表为空*/
	public E getLast(){
		try{
			return get(size - 1);
		}catch(IndexOutOfBoundsException e){
			throw new NoSuchElementException(); 
		}
	}
	
	/**获取链表指定位置的元素，
	 * @param index 指定链表上位置的下标，从0开始
	 * @return 链表在该位置上的元素
	 * @throws IndexOutOfBoundsException 如果下标超出范围
	 * (index < 0 || index > size() - 1)*/
	@Override
	public E get(int index) {
		// TODO Auto-generated method stub
		checkElementIndex(index);
		
		Node<E> current = head;
		for(int i = 0;i < index;i ++)
			current = current.next;
		return current.element;
	}

	
	@Override
	public int indexOf(E e) {
		// TODO Auto-generated method stub
		Node<E> current = head;
		int index = 0;
		
		//条件1：“current 1= null”；条件2“!current.element.equals(e)”
		//条件1应放在条件2的前面，并在其间用“&&”连接，
		//	因为前者是后者的必要条件。
		//	条件1的含义是：遍历没有结束。
		//遍历结束有两种情况：
		//	1.链表为空 2.current = tail.next
		while(current != null && !current.element.equals(e)){
			current = current.next;
			index ++;
		}
		
		
		return current == null ? -1:index;
	}

	@Override
	public int lastIndexOf(E e) {
		// TODO Auto-generated method stub
		Node<E> current = head;
		int index = -1;
		for(int i = 0;current != null;i ++){
			if(current.element.equals(e))
				index = i;
			current = current.next;
		}
		
		return index;
	}

	/**当链表不空时，删除链表的第一个元素，并返回该元素的值。
	 * @return 被删除元素的值
	 * @throws NoSuchElementException 链表为空*/
	public E removeFirst(){
		try{
			return remove(0);
		}catch(IndexOutOfBoundsException e){
			throw new NoSuchElementException(); 
		}
	}
	
	/**当链表不空时，删除链表最后元素，并返回该元素的值。
	 * @return 被删除元素的值
	 * @throws NoSuchElementException 链表为空*/
	public E removeLast(){
		try{
			return remove(size - 1);
		}catch(IndexOutOfBoundsException e){
			throw new NoSuchElementException(); 
		}
	}

	@Override
	public E remove(int index) {
		// TODO Auto-generated method stub
		checkElementIndex(index);
		
		Node<E> removedNode ;
		
		/*通过了下标检查
		 *已经足以说明链表不空。*/
		 if(size == 1){
			removedNode = head;
			head = tail = null;
		}else if(index == 0){//删除表头
			removedNode = head;
			head = head.next;
		}else{//0 < index <= size - 1 && size >= 2
			Node<E> previous  = head;
			for(int i = 0;i < index - 1;i ++)
				previous = previous.next;
			removedNode = previous.next;
			previous.next = removedNode.next;
			
			//如果删除的是表尾，就对表尾指针做修改。
			//否则表尾指针不变
			tail = index == size - 1 ?previous: tail;
		}
		size --;	
					
		return removedNode.element;
	}

	
	/**设置链表上指定位置的元素。
	 * @param index
	 * @param e
	 * @return 链表在修改前该位置原有的元素
	 * @throws IndexOutOfBoundsException*/
	@Override
	public E set(int index, E e) {
		// TODO Auto-generated method stub
		checkElementIndex(index);
		
		Node<E> current = head;
		for(int i = 0;i < index;i++)
			current = current.next;
		E oldElement = current.element;
		current.element = e;
		return oldElement;
	}
	
	private void checkPositionIndex(int index){
		if(index < 0 || index > size)
			throw new IndexOutOfBoundsException(outOfBoundsMsg(index));		
	}
	
	private void checkElementIndex(int index){
		if(index < 0 || index > size - 1)
			throw new IndexOutOfBoundsException(outOfBoundsMsg(index));		
	}

	private String outOfBoundsMsg(int index){
		return "Index: " + index + " Size: " + size;
	}
	
	public String toString(){
		StringBuilder result = new StringBuilder("[ ");
		Node<E> current = head;
		for(int i = 0;i < size;i ++){			
			result.append(current.element.toString());
			result.append(i == size - 1?"":", ");
			current = current.next;					
		}
		result.append(" ]");	
		
		
		return result.toString();
	}
	
	@Override
	public Iterator<E> iterator() {
		// TODO Auto-generated method stub
		return new MyIterator();
	}
	
	private class MyIterator implements Iterator<E>{
		//仿AbstractLIst设计
		
		int cursor;
		
		@Override
		public boolean hasNext() {
			// TODO Auto-generated method stub
			return cursor != size;
		}

		@Override
		public E next() {
			// TODO Auto-generated method stub
			try{
				int i = cursor;
				cursor ++;
				return get(i);		
			}catch(IndexOutOfBoundsException e){
				//捕捉“下标越界”异常后再换“该元素不存在”异常
				//因为该函数调用时没有用到下标。
				throw new NoSuchElementException();
			}
		}
	}

	private static class Node<E>{
		E element;
		Node<E> next;
		public Node(E element){
			this.element = element;
		}
	}
}
