import numpy as np
import pandas as pd

def generate_random_qubo(n: int, low: int = -50, high: int = 50, seed: int = None) -> np.ndarray:
    """
    Genera una instancia QUBO aleatoria.
    
    Args:
        n (int): Tamaño de la matriz Q (n x n)
        low (int): Límite inferior (inclusive)
        high (int): Límite superior (inclusive)
        seed (int, opcional): Semilla para reproducibilidad
    
    Returns:
        np.ndarray: Matriz Q simétrica de tamaño n x n
    """
    if seed is not None:
        np.random.seed(seed)

    # Generar números aleatorios enteros en el rango [low, high], excluyendo 0
    Q = np.random.randint(low, high + 1, size=(n, n))
    Q[Q == 0] = np.random.choice(
        np.concatenate((np.arange(low, 0), np.arange(1, high + 1))),
        size=np.sum(Q == 0)
    )
    
    # Hacer la matriz simétrica (QUBO típica)
    Q = np.triu(Q)  # parte superior
    Q = Q + Q.T - np.diag(np.diag(Q))
    
    return Q

# Ejemplo de uso:
if __name__ == "__main__":
    n = 1024 # tamaño de la instancia QUBO
    Q = generate_random_qubo(n, seed=42)
    df = pd.DataFrame(Q)
    df.to_csv("r1k_50.csv", index=False, header=False)
    print("Matriz QUBO aleatoria:\n", Q)
