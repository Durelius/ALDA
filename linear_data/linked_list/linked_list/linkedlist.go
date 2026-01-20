// Package linkedlist ..
package linkedlist

import (
	"errors"
	"fmt"
	"reflect"
	"strings"
)

type LinkedList[T comparable] struct {
	head *node[T]
	tail *node[T]
	size int
}

type node[T comparable] struct {
	Data T
	Next *node[T]
}

var (
	ErrOutOfBounds = errors.New("out of bounds")
	ErrWrongType   = errors.New("wrong type compared to head")
	ErrNotFound    = errors.New("obj not found")
)

func (ll *LinkedList[T]) Insert(data T) error {
	if ll.head == nil {
		node := &node[T]{Data: data}
		ll.head = node
		ll.tail = node
		ll.size = 1
		return nil
	}

	if reflect.TypeOf(ll.head.Data) != reflect.TypeOf(data) {
		return errors.New("not same type in linked list")
	}

	ll.tail.Next = &node[T]{Data: data}
	ll.tail = ll.tail.Next
	ll.size++
	return nil
}

func (ll *LinkedList[T]) Size() int {
	return ll.size
}

func (ll *LinkedList[T]) Head() (T, error) {
	if ll.head == nil {
		return node[T]{}.Data, errors.New("list is empty")
	}
	return ll.head.Data, nil
}

func (ll *LinkedList[T]) Tail() (T, error) {
	if ll.tail == nil {
		return node[T]{}.Data, errors.New("list is empty")
	}
	return ll.tail.Data, nil
}

func (ll *LinkedList[T]) GetI(index int) (T, error) {
	if index == ll.size-1 {
		return ll.tail.Data, nil
	}
	if index >= ll.size {
		return node[T]{}.Data, ErrOutOfBounds
	}
	curNode := ll.head

	for range index {
		if curNode == nil {
			return node[T]{}.Data, ErrNotFound
		}
		curNode = curNode.Next
	}
	return curNode.Data, nil
}

func (ll *LinkedList[T]) ObjExists(obj T) bool {
	curNode := ll.head
	for curNode != nil {
		if curNode.Data == obj {
			return true
		}
		curNode = curNode.Next
	}
	return false
}

func (ll *LinkedList[T]) DeleteI(index int) error {
	if index < 0 || index >= ll.size {
		return ErrOutOfBounds
	}
	node := ll.head
	if index == 0 {
		ll.size--
		ll.head = node.Next
		return nil
	}
	for range index - 1 {
		node = node.Next
	}
	node.Next = node.Next.Next
	if index == ll.size-1 {
		ll.tail = node.Next
	}
	ll.size--
	return nil
}

func (ll *LinkedList[T]) DeleteO(obj T) error {
	if ll.head == nil {
		return ErrNotFound
	}

	// Case 1: deleting head
	if ll.head.Data == obj {
		ll.head = ll.head.Next
		ll.size--
		if ll.head == nil {
			ll.tail = nil
		}
		return nil
	}

	// Case 2: deleting non-head
	prev := ll.head
	for prev.Next != nil {
		if prev.Next.Data == obj {
			// deleting tail
			if prev.Next == ll.tail {
				ll.tail = prev
			}
			prev.Next = prev.Next.Next
			ll.size--
			return nil
		}
		prev = prev.Next
	}

	return ErrNotFound
}

func (ll *LinkedList[T]) String() string {
	var b strings.Builder
	b.WriteString("[")
	for cur := ll.head; cur != nil; cur = cur.Next {
		fmt.Fprintf(&b, "%v", cur.Data)
		if cur.Next != nil {
			b.WriteString(", ")
		}
	}
	b.WriteString("]")
	return b.String()
}
