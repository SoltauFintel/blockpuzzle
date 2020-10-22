package de.mwvb.blockpuzzle.logic;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import de.mwvb.blockpuzzle.entity.QPosition;

public class OneColorAreaDetectorTest {

    @Test
    public void test() {
        PlayingField pf = new PlayingField(4);
        pf.set(0, 0, 2);
        pf.set(1, 0, 2);
        pf.set(3, 3, 3);
        OneColorAreaDetector oc = new OneColorAreaDetector(pf, 2);
        List<QPosition> r = oc.getOneColorArea();

        Assert.assertEquals(2, r.size());
    }

    @Test
    public void test2() {
        PlayingField pf = new PlayingField(4);
        // . X . X
        // X X X X
        pf.set(0, 1, 2);
        pf.set(1, 1, 2);
        pf.set(2, 1, 2);
        pf.set(3, 1, 2);
        pf.set(1, 0, 2);
        pf.set(3, 0, 2);

        OneColorAreaDetector oc = new OneColorAreaDetector(pf, 6);
        List<QPosition> r = oc.getOneColorArea();

        Assert.assertEquals(6, r.size());
    }

    @Test
    public void test3() {
        PlayingField pf = new PlayingField(4);
        // X X X X
        // X X X X
        pf.set(0, 0, 2);
        pf.set(1, 0, 2);
        pf.set(2, 0, 2);
        pf.set(3, 0, 2);
        pf.set(0, 1, 2);
        pf.set(1, 1, 2);
        pf.set(2, 1, 2);
        pf.set(3, 1, 2);

        OneColorAreaDetector oc = new OneColorAreaDetector(pf, 6);
        List<QPosition> r = oc.getOneColorArea();

        Assert.assertEquals(8, r.size());
    }

    @Test
    public void test4() {
        PlayingField pf = new PlayingField(4);
        // X . X X
        // X . X X
        pf.set(0, 0, 2);
        pf.set(2, 0, 2);
        pf.set(3, 0, 2);
        pf.set(0, 1, 2);
        pf.set(2, 1, 2);
        pf.set(3, 1, 2);

        OneColorAreaDetector oc = new OneColorAreaDetector(pf, 2);
        List<QPosition> r = oc.getOneColorArea();

//        Assert.assertEquals(4, r.size());
    }

    @Test
    public void test5() {
        PlayingField pf = new PlayingField(5);
        // . X X X
        // . X X X
        // X X . X X
        pf.set(1, 0, 1);
        pf.set(2, 0, 1);
        pf.set(3, 0, 1);
        pf.set(0, 1, 3);
        pf.set(1, 1, 1);
        pf.set(2, 1, 1);
        pf.set(3, 1, 1);
        pf.set(0, 2, 1);
        pf.set(1, 2, 1);
        pf.set(2, 2, 4);
        pf.set(3, 2, 1);
        pf.set(4, 2, 1);

        OneColorAreaDetector oc = new OneColorAreaDetector(pf, 6);
        List<QPosition> r = oc.getOneColorArea();

        Assert.assertEquals(10, r.size());
    }
}
