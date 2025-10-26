public class PenRodBuilder implements ILIneStep {
    public PenRodBuilder() {
        System.out.println("Created PenRodBuilder");
    }

    @Override
    public IProductPart builProductPart() {
        System.out.println("PenRod builded");
        return new PenRod();
    }
}