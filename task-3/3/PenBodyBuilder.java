public class PenBodyBuilder implements ILIneStep {
    public PenBodyBuilder() {
        System.out.println("Created PenBodyBuilder");
    }

    @Override
    public IProductPart builProductPart() {
        System.out.println("PenBody builded");
        return new PenBody();
    }
}