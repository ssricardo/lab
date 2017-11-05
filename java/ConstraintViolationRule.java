package br.org.rss.test;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class ConstraintViolationRule implements TestRule {

	private static ConstraintViolationRule instance;

	private ConstraintViolationRule() {
	}

	public static ConstraintViolationRule getInstance() {
		if (null == instance) {
			instance = new ConstraintViolationRule();
		}
		return instance;
	}

	@Override
	public Statement apply(final Statement base, Description description) {
		return new WrappedStatement(base);
	}

	class WrappedStatement extends Statement {

		private Statement base;

		public WrappedStatement(Statement base) {
			super();
			this.base = base;
		}

		@Override
		public void evaluate() throws Throwable {
			try {
				base.evaluate();
			} catch (Exception e) {
				ConstraintViolationException cve = null;

				if (e instanceof ConstraintViolationException) {
					cve = (ConstraintViolationException) e;
				} else if (e.getCause() != null && (e.getCause() instanceof ConstraintViolationException)) {
					cve = (ConstraintViolationException) e.getCause();
				}

				if (cve != null) {
					Set<ConstraintViolation<?>> fails = cve.getConstraintViolations();

					System.err.println("--------------- ConstraintViolation gotten ---------------");
					for (ConstraintViolation<?> violation : fails) {
						System.out.println(violation);
					}
				}
				throw e;
			}
		}

	}

}
