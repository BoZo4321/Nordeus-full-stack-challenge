using UnityEngine;

public class GameManager : MonoBehaviour
{
    private static GameManager _instance;
    public static GameManager Instance => _instance;

    public string CurrentRunId { get; set; }
    public string CurrentBattleId { get; set; }
    public StartRunResponse CurrentRun { get; set; }
    public StartBattleResponse CurrentBattle { get; set; }

    void Awake()
    {
        if (_instance == null)
        {
            _instance = this;
            DontDestroyOnLoad(gameObject);
        }
        else
        {
            Destroy(gameObject);
        }
    }
}
