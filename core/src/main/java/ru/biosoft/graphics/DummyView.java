package ru.biosoft.graphics;

public class DummyView extends View
{
    public DummyView(Object model, boolean active)
    {
        super(null);
        setActive(active);
        setModel(model);
    }
    
    @Override
    public void move(int x, int y) 
    {
    	// stub
    }
}
 