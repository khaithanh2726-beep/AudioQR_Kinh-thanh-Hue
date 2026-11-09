package vn.hueqr.backend.service;

public interface TtsProvider {
	byte[] generate(TtsDispatchService.DispatchLease input) throws Failure;

	class Failure extends Exception {
		private final String code;
		private final boolean definite;

		public Failure(String code, boolean definite) {
			super(code);
			this.code = code;
			this.definite = definite;
		}

		public String code() {
			return code;
		}

		public boolean definite() {
			return definite;
		}
	}
}
