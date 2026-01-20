package linkedlist

import (
	"testing"
)

func TestInsertAndSize(t *testing.T) {
	var ll LinkedList[int]

	if ll.Size() != 0 {
		t.Errorf("expected size 0, got %d", ll.Size())
	}

	if err := ll.Insert(1); err != nil {
		t.Errorf("unexpected error: %v", err)
	}

	if ll.Size() != 1 {
		t.Errorf("expected size 1, got %d", ll.Size())
	}

	if err := ll.Insert(2); err != nil {
		t.Errorf("unexpected error: %v", err)
	}

	if ll.Size() != 2 {
		t.Errorf("expected size 2, got %d", ll.Size())
	}
}

func TestHeadAndTail(t *testing.T) {
	var ll LinkedList[int]

	_, err := ll.Head()
	if err == nil {
		t.Fatal("expected error on empty list head")
	}

	_ = ll.Insert(10)
	_ = ll.Insert(20)

	head, _ := ll.Head()
	tail, _ := ll.Tail()

	if head != 10 {
		t.Errorf("expected head 10, got %v", head)
	}

	if tail != 20 {
		t.Errorf("expected tail 20, got %v", tail)
	}
}

func TestGetI(t *testing.T) {
	var ll LinkedList[int]
	_ = ll.Insert(1)
	_ = ll.Insert(2)
	_ = ll.Insert(3)
	for i := range 3 {
		v, err := ll.GetI(i)
		if err != nil {
			t.Errorf("unexpected error: %v", err)
		}

		if v != i+1 {
			t.Errorf("expected %d, got %v", i+1, v)
		}
	}
}

func TestGetIOutOfBounds(t *testing.T) {
	var ll LinkedList[int]
	_ = ll.Insert(1)

	_, err := ll.GetI(5)
	if err != ErrOutOfBounds {
		t.Errorf("expected ErrOutOfBounds, got %v", err)
	}
}

func TestObjExists(t *testing.T) {
	var ll LinkedList[int]
	_ = ll.Insert(1)
	_ = ll.Insert(2)
	_ = ll.Insert(3)

	v := ll.ObjExists(2)

	if !v {
		t.Errorf("expected true, got %v", v)
	}
}

func TestDeleteI(t *testing.T) {
	var ll LinkedList[int]
	_ = ll.Insert(1)
	_ = ll.Insert(2)
	_ = ll.Insert(3)

	if ll.Size() != 3 {
		t.Errorf("expected size 3, got %d", ll.Size())
	}
	if err := ll.DeleteI(1); err != nil {
		t.Errorf("unexpected error: %v", err)
	}

	if ll.Size() != 2 {
		t.Errorf("expected size to be 2, got %d", ll.Size())
	}

	v, _ := ll.GetI(1)
	if v != 3 {
		t.Errorf("expected value 3 after deletion, got %v", v)
	}
}

func TestDeleteIOutOfBounds(t *testing.T) {
	var ll LinkedList[int]
	_ = ll.Insert(1)

	err := ll.DeleteI(5)
	if err != ErrOutOfBounds {
		t.Errorf("expected ErrOutOfBounds, got %v", err)
	}
}

func TestDeleteO(t *testing.T) {
	var ll LinkedList[int]
	_ = ll.Insert(1)
	_ = ll.Insert(2)
	_ = ll.Insert(3)
	if err := ll.DeleteO(2); err != nil {
		t.Errorf("unexpected error: %v", err)
	}

	v, _ := ll.GetI(1)
	if v != 3 {
		t.Errorf("expected value 3 after delete, got %v", v)
	}
}

func TestDeleteONotFound(t *testing.T) {
	var ll LinkedList[int]
	_ = ll.Insert(1)
	_ = ll.Insert(2)

	err := ll.DeleteO(99)
	if err != ErrNotFound {
		t.Errorf("expected ErrNotFound, got %v", err)
	}
}
