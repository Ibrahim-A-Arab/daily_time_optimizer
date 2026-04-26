public class MyArrayList<T> {

    private Object[] data;
    private int size;

    public MyArrayList() {
        data = new Object[50];
        size = 0;
    }

    public int size() {
        return size;
    }

    public T get(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException();
        return (T) data[index];
    }

    public void add(T element) {
        if (size == data.length)
            resize();
        data[size++] = element;
    }

    public void set(int index, T element) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException();
        data[index] = element;
    }

    public void remove(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException();

        for (int i = index; i < size - 1; i++) {
            data[i] = data[i + 1];
        }
        data[--size] = null;
    }

    private void resize() {
        Object[] newData = new Object[data.length * 2];// 5^2=5*5 -> would be good for 5+ and large numbers				
        for (int i = 0; i < data.length; i++) {
            newData[i] = data[i];
        }
        data = newData;
    }
    public void clear() {
		data = new Object[50];
		size = 0;
	}
    public boolean isEmpty() {
    	return size==0;
    }
    public int getIndex(T element) {
        if (element == null) return -1;

        for (int i = 0; i < size; i++) {
            if (element.equals(data[i])) {
                return i;
            }
        }
        return -1;
    }

    
}

