public class Pen implements IProduct {
    private PenBody body;
    private PenSpring spring;
    private PenRod rod;

    public Pen() {
        System.out.println("Created blank Pen");
    }

    @Override
    public void installFirstPart(IProductPart part) {
        body = (PenBody) part;
        System.out.println("Pen first part installed");
    }

    @Override
    public void installSecondPart(IProductPart part) {
        spring = (PenSpring) part;
        System.out.println("Pen second part installed");
    }

    @Override
    public void installThirdPart(IProductPart part) {
        rod = (PenRod) part;
        System.out.println("Pen third part installed");
    }
}
