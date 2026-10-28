package dev.tollgate.simulator;

public record FraudMix(int irsf, int wangiri, int smsPumping, int simBox) {
    public FraudMix {
        if (irsf < 0 || wangiri < 0 || smsPumping < 0 || simBox < 0) {
            throw new IllegalArgumentException("fraud counts cannot be negative: %s".formatted(
                    java.util.List.of(irsf, wangiri, smsPumping, simBox)));
        }
    }

    public int total() {
        return irsf + wangiri + smsPumping + simBox;
    }
}
