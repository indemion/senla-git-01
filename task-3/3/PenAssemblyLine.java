public class PenAssemblyLine implements IAssemblyLine {
    private final PenBodyBuilder firstStep;
    private final PenSpringBuilder secondStep;
    private final PenRodBuilder thirdStep;

    public PenAssemblyLine(PenBodyBuilder firstStep, PenSpringBuilder secondStep, PenRodBuilder thirdStep) {
        this.firstStep = firstStep;
        this.secondStep = secondStep;
        this.thirdStep = thirdStep;
        System.out.println("Created PenAssemblyLine");
    }

    @Override
    public IProduct assembleProduct(IProduct product) {
        IProductPart firstPart = firstStep.builProductPart();
        IProductPart secondPart = secondStep.builProductPart();
        IProductPart thirdPart = thirdStep.builProductPart();

        product.installFirstPart(firstPart);
        product.installSecondPart(secondPart);
        product.installThirdPart(thirdPart);

        return product;
    }
}