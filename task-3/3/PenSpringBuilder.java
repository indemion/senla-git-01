public class PenSpringBuilder implements ILIneStep {
    public PenSpringBuilder() {
        System.out.println("Created PenSpringBuilder");
    }

    @Override
    public IProductPart builProductPart() {
        System.out.println("PenSpring builded");
        return new PenSpring();
    }
}