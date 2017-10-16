package com.liux.list.adapter;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * 包裹条目状态的 ArrayList
 * @param <T>
 */
public class StateList<T> extends ArrayList<T> {

    private List<State<T>> mStates = new ArrayList<State<T>>();

    public static <T> StateList<T> from(List<T> data) {
        StateList<T> stateList = new StateList<T>();
        if (data != null) {
            stateList.addAll(data);
        }
        return stateList;
    }

    @Override
    public boolean add(T t) {
        mStates.add(new State<T>(t));
        return super.add(t);
    }

    @Override
    public boolean remove(Object o) {
        int index = indexOf(o);
        if (index != -1) {
            mStates.remove(index);
        }
        return super.remove(o);
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends T> c) {
        List<State<T>> list = new ArrayList<>();
        for (T t : c) {
            list.add(new State<T>(t));
        }
        mStates.addAll(list);
        return super.addAll(c);
    }

    @Override
    public boolean addAll(int index, @NonNull Collection<? extends T> c) {
        List<State<T>> list = new ArrayList<>();
        for (T t : c) {
            list.add(new State<T>(t));
        }
        mStates.addAll(index, list);
        return super.addAll(index, c);
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> c) {
        Iterator<State<T>> iterator = mStates.iterator();
        while (iterator.hasNext()) {
            State<T> state = iterator.next();
            if (c.contains(state.data)) {
                iterator.remove();
            }
        }
        return super.removeAll(c);
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> c) {
        Iterator<State<T>> iterator = mStates.iterator();
        while (iterator.hasNext()) {
            State<T> state = iterator.next();
            if (!c.contains(state.data)) {
                iterator.remove();
            }
        }
        return super.retainAll(c);
    }

    @Override
    public void clear() {
        mStates.clear();
        super.clear();
    }

    @Override
    public T set(int index, T element) {
        mStates.set(index, new State<T>(element));
        return super.set(index, element);
    }

    @Override
    public void add(int index, T element) {
        mStates.add(index, new State<T>(element));
        super.add(index, element);
    }

    @Override
    public T remove(int index) {
        mStates.remove(index);
        return super.remove(index);
    }

    public State<T> getState(int index) {
        return mStates.get(index);
    }

    public State<T> getState(T t) {
        int index = indexOf(t);
        if (index != -1) {
            return mStates.get(index);
        }
        return null;
    }

    public void setState(int index, int state) {
        mStates.get(index).state = state;
    }

    public void setState(T t, int state) {
        int index = indexOf(t);
        if (index != -1) {
            mStates.get(index).state = state;
        }
    }

    public List<State<T>> getStates() {
        return mStates;
    }

    public void setStateAll(int state) {
        for (State<T> s : mStates) {
            s.state = state;
        }
    }

    public List<T> getStateAll(int state) {
        List<T> ts = new ArrayList<T>();
        for (State<T> s : mStates) {
            if (s.state == state) ts.add(s.data);
        }
        return ts;
    }

    public int getStateAllCount(int state) {
        int count = 0;
        for (State<T> s : mStates) {
            if (s.state == state) count++;
        }
        return count;
    }

    public void reverseStateAll() {
        for (State<T> s : mStates) {
            if (s.state == State.STATE_SELECTED) {
                s.state = State.STATE_UNSELECTED;
            } else {
                s.state = State.STATE_SELECTED;
            }
        }
    }
}
